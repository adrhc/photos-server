package image.exifwebtests.jsp;

import image.exifweb.RootConfig;
import image.exifweb.WebConfig;
import image.jpa2xtests.config.profiles.InMemoryDbProfile;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextHierarchy({
		@ContextConfiguration(classes = {RootConfig.class}),
		@ContextConfiguration(classes = {WebConfig.class})
})
@WebAppConfiguration
@TestPropertySource(properties = "hibernate.show_sql=true")
@InMemoryDbProfile
@Tag("junit5")
@Tag("inmemorydb")
@Tag("web")
class IndexJspControllerTest {
	@Test
	void test() {}
}
