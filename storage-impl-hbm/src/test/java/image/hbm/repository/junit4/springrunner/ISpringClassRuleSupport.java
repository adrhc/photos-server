package image.hbm.repository.junit4.springrunner;

import org.junit.ClassRule;
import org.springframework.test.context.junit4.rules.SpringClassRule;

/**
 * Created by adr on 2/24/18.
 */
public interface ISpringClassRuleSupport {
	@ClassRule
	SpringClassRule springClassRule = new SpringClassRule();
}
