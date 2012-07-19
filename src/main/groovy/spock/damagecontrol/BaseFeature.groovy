package spock.damagecontrol

abstract class BaseFeature {

    final featureDefinitionParser = new FeatureDefinitionParser()
    final featureStepsParser = new StepDefinitionParser()
    final steps = []

    def name
    def duration

    def parseDefinition(specSourceCode) {
        steps.addAll(featureStepsParser.parse(featureDefinitionParser.parse(name, specSourceCode)))
    }
}
