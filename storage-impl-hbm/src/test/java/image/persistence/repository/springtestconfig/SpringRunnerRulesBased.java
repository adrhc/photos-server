package image.persistence.repository.springtestconfig;

import org.junit.Rule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

/**
 * Created by adr on 2/25/18.
 */
public abstract class SpringRunnerRulesBased implements ISpringClassRuleSupport {
	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();
}
