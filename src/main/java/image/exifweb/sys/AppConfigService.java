package image.exifweb.sys;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.exifweb.persistence.AppConfig;
import image.exifweb.persistence.AppConfigEnum;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppConfigService {
    private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);
    @Inject
    private ObjectMapper json;
    @Inject
    private SessionFactory sessionFactory;

    public Integer getConfigInteger(AppConfigEnum ace) {
        String s = getConfig(ace);
        if (s == null) {
            return null;
        }
        return new Integer(s);
    }

    public int getPhotosPerPage() {
        return getConfigInt(AppConfigEnum.PHOTOS_PER_PAGE);
    }

    public String getLinuxAlbumPath() {
        return getConfig(AppConfigEnum.LINUX_ALBUMS_PATH);
    }

    public Integer getConfigInt(AppConfigEnum ace) {
        String s = getConfig(ace);
        if (s == null) {
            return 0;
        }
        return Integer.parseInt(s);
    }

    public String getConfig(AppConfigEnum ace) {
        List<AppConfig> appConfigs = getAppConfigs();
        for (AppConfig ac : appConfigs) {
            if (ac.getId().equals(ace.getValue())) {
                return ac.getValue();
            }
        }
        return null;
    }

    public boolean getConfigBool(String name) {
        String s = getConfig(name);
        return Boolean.parseBoolean(s);
    }

    public Boolean getConfigBoolean(String name) {
        String s = getConfig(name);
        return Boolean.valueOf(s);
    }

    public Integer getConfigInt(String name) {
        String s = getConfig(name);
        if (s == null) {
            return 0;
        }
        return Integer.parseInt(s);
    }

    public String getConfig(String name) {
        AppConfig ac = getAppConfig(name);
        if (ac == null) {
            return null;
        }
        return ac.getValue();
    }

    public AppConfig getAppConfig(String name) {
        List<AppConfig> appConfigs = getAppConfigs();
        for (AppConfig ac : appConfigs) {
            if (ac.getName().equals(name)) {
                return ac;
            }
        }
        return null;
    }

    @Transactional
    @CacheEvict(value = "appConfig", allEntries = true)
    public void update(List<AppConfig> appConfigs) {
        Session session = sessionFactory.getCurrentSession();
        List<AppConfig> dbAppConfigs = session.createCriteria(AppConfig.class).list();
        for (AppConfig dbAppConfig : dbAppConfigs) {
            for (AppConfig appConfig : appConfigs) {
                if (dbAppConfig.getId().equals(appConfig.getId())) {
                    dbAppConfig.setValue(appConfig.getValue());
                    break;
                }
            }
        }
    }

    /**
     * Metoda cu dublu scope:
     * - update appConfigs.json
     * - update lastUpdatedAppConfigs
     *
     * @throws IOException
     */
    public void writeJsonForAppConfigs() throws IOException {
        File dir = new File(getConfig("photos json FS path"));
        dir.mkdirs();
        File file = new File(dir, "appConfigs.json");
        List<AppConfig> appConfigs = getAppConfigs();
//        logger.debug(ArrayUtils.toString(appConfigs));
//        logger.debug("lastUpdatedAppConfigs = {}", getLastUpdatedAppConfigs());
        json.writeValue(file, appConfigs);
    }

    @Transactional
    @Cacheable(value = "appConfig", key = "'appConfigs'")
    public List<AppConfig> getAppConfigs() {
        Session session = sessionFactory.getCurrentSession();
        List<AppConfig> ret = session.createCriteria(AppConfig.class).list();
        Hibernate.initialize(ret);
        return ret;
    }

    @Cacheable(value = "appConfig", key = "'lastUpdatedAppConfigs'")
    public long getLastUpdatedAppConfigs() {
//        logger.debug("BEGIN");
        List<AppConfig> appConfigs = getAppConfigs();
        Date date = null;
        for (AppConfig appConfig : appConfigs) {
            if (date == null) {
                date = appConfig.getLastUpdate();
            } else if (date.before(appConfig.getLastUpdate())) {
                date = appConfig.getLastUpdate();
            }
//            logger.debug("{} = {}, date = " + date.getTime(),
//                appConfig.getName(), appConfig.getLastUpdate().getTime());
        }
//        logger.debug("END {}", date.getTime());
        return date.getTime();
    }

    @Cacheable(value = "appConfig", key = "'canUseJsonFilesLastUpdate'")
    public long canUseJsonFilesLastUpdate() {
        AppConfig useJsonFiles = getAppConfig("use json files");
        AppConfig useJsonFilesForConfig = getAppConfig("use json files for config");
        if (useJsonFiles.getLastUpdate().after(useJsonFilesForConfig.getLastUpdate())) {
//            logger.debug("END {}", useJsonFiles.getLastUpdate().getTime());
            return useJsonFiles.getLastUpdate().getTime();
        } else {
//            logger.debug("END {}", useJsonFilesForConfig.getLastUpdate().getTime());
            return useJsonFilesForConfig.getLastUpdate().getTime();
        }
    }

    @Transactional
    public Date getDBNow() {
        Session session = sessionFactory.getCurrentSession();
        return (Date) session.createSQLQuery("SELECT now() FROM dual").uniqueResult();
    }
}
