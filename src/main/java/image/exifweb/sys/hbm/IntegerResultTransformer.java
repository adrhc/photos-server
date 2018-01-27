package image.exifweb.sys.hbm;

import org.hibernate.transform.ResultTransformer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 11/9/13
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntegerResultTransformer implements ResultTransformer {
	public static final IntegerResultTransformer INSTANCE = new IntegerResultTransformer();

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		if (tuple[0] == null) {
			return null;
		}
		if (tuple[0] instanceof Integer) {
			return tuple[0];
		}
		return new Integer(((Number) tuple[0]).intValue());
	}

	@Override
	public List transformList(List collection) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
