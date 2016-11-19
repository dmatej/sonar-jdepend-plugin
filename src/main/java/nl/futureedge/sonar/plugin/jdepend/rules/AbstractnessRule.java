package nl.futureedge.sonar.plugin.jdepend.rules;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import jdepend.framework.JavaPackage;
import nl.futureedge.sonar.plugin.jdepend.JdependRulesDefinition;

/**
 * Abstractness rule.
 *
 * @see JavaPackage#abstractness()
 */
public class AbstractnessRule extends AbstractRule implements Rule {

	private static final Logger LOGGER = Loggers.get(AbstractnessRule.class);

	private final Integer maximum;

	/**
	 * Constructor.
	 *
	 * @param context
	 *            sensor context
	 */
	public AbstractnessRule(final SensorContext context) {
		super(context, JdependRulesDefinition.ABSTRACTNESS_RULE);

		maximum = getParamAsInteger(JdependRulesDefinition.PARAM_MAXIMUM);
		if (maximum == null) {
			LOGGER.info("Rule {} activated, no value for parameter {} set. Disabling rule...", getKey(),
					JdependRulesDefinition.PARAM_MAXIMUM);
			disable();
		}
	}

	@Override
	public void execute(final JavaPackage javaPackage, final InputFile packageInfoFile) {
		if (!isActive()) {
			return;
		}

		final int abstractness = Math.round(javaPackage.abstractness() * 100);

		if (abstractness > maximum) {
			final NewIssue issue = getContext().newIssue().forRule(getKey());
			issue.at(issue.newLocation().on(packageInfoFile).at(packageInfoFile.selectLine(1))
					.message("Too much abstractness (allowed: " + maximum + ", actual: " + abstractness + ")"));
			issue.save();
		}
	}
}
