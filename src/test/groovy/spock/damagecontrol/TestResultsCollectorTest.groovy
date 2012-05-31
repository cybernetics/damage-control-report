package spock.damagecontrol

import static org.apache.commons.io.FileUtils.copyFileToDirectory

class TestResultsCollectorTest extends BaseFileHandlingSpec {

    private static final String SAMPLE_FOLDER = 'src/test/resources/samples/results'

    private static final File XML_WITH_ONE_TEST_CASE = new File(SAMPLE_FOLDER + '/TEST-spock.damagecontrol.TestResultsParserTest.xml')
    private static final File XML_WITH_TWO_TEST_CASES = new File(SAMPLE_FOLDER + '/TEST-spock.damagecontrol.TestResultsWith2TestCases.xml')
    private static final File XML_WITH_NO_TEST_CASE = new File(SAMPLE_FOLDER + '/TEST-no-test-case.xml')
    private static final File XML_WITH_IGNORED_TEST_CASE = new File(SAMPLE_FOLDER + '/TEST-spock.damagecontrol.TestResultsWithIgnoredTestCase.xml')
    private static final File XML_WITH_SYS_OUT = new File(SAMPLE_FOLDER + '/TEST-spock.damagecontrol.TestResultsWithSysOut.xml')
    private static final File XML_WITHOUT_SYS_OUT = new File(SAMPLE_FOLDER + '/TEST-spock.damagecontrol.TestResultsWithoutSysOut.xml')
    private static final File EMPTY = new File(SAMPLE_FOLDER + '/empty.xml')

    private Map specs = [:]

    private TestResultsCollector collector

    private Closure toSpecsMap = { spec ->
        specs[spec.name] = spec
    }

    def setup() {
        collector = new TestResultsCollector(testFolder)
    }

    def 'should collect all specification names in the folder'() {
        given:
        copyFileToDirectory(XML_WITH_ONE_TEST_CASE, testFolder)
        copyFileToDirectory(XML_WITH_TWO_TEST_CASES, testFolder)

        when:
        collector.forEach(toSpecsMap);

        then:
        specs['spock.damagecontrol.TestResultsCollectorTest1']
        specs['spock.damagecontrol.TestResultsCollectorTest2']
        specs['spock.damagecontrol.AnotherTestResultsCollectorTest']
    }

    def 'should collect all features for the same specification'() {
        given:
        copyFileToDirectory(XML_WITH_TWO_TEST_CASES, testFolder)

        when:
        collector.forEach(toSpecsMap);

        then:
        specs['spock.damagecontrol.AnotherTestResultsCollectorTest'].features['shouldParseXml']
        specs['spock.damagecontrol.AnotherTestResultsCollectorTest'].features['shouldFail']
    }

    def 'should collect failure message for each feature'() {
        given:
        copyFileToDirectory(XML_WITH_TWO_TEST_CASES, testFolder)

        when:
        collector.forEach(toSpecsMap);

        and:
        Feature feature = specs['spock.damagecontrol.AnotherTestResultsCollectorTest'].features['shouldFail']

        then:
        feature.failure.message == 'java.lang.AssertionError: \nExpected: is <true>\n     got: <false>\n'
    }

    def 'should collect ignored features'() {
        given:
        copyFileToDirectory(XML_WITH_IGNORED_TEST_CASE, testFolder)

        when:
        collector.forEach(toSpecsMap);

        and:
        Feature feature = specs['spock.damagecontrol.TestResultsWithIgnoredTestCase'].features['ignored feature']

        then:
        feature.ignored
    }

    def 'should collect skipped features'() {
        given:
        copyFileToDirectory(XML_WITH_IGNORED_TEST_CASE, testFolder)

        when:
        collector.forEach(toSpecsMap);

        and:
        Feature feature = specs['spock.damagecontrol.TestResultsWithIgnoredTestCase'].features['skipped feature']

        then:
        feature.ignored
    }

    def 'should collect failure details for each feature'() {
        given:
        copyFileToDirectory(XML_WITH_TWO_TEST_CASES, testFolder)

        when:
        collector.forEach(toSpecsMap);

        and:
        Feature feature = specs['spock.damagecontrol.AnotherTestResultsCollectorTest'].features['shouldFail']

        then:
        feature.failure.details.contains('at spock.damagecontrol.TestResultsCollectorTest.shouldFail(TestResultsParserTest.groovy:19)')
    }

    def 'should collect standard output for spec'() {
        given:
        copyFileToDirectory(XML_WITH_SYS_OUT, testFolder)

        when:
        collector.forEach(toSpecsMap);

        then:
        specs['spock.damagecontrol.TestResultsWithSysOut'].output.standard == 'standard output message'
    }

    def 'should collect results without standard output'() {
        given:
        copyFileToDirectory(XML_WITHOUT_SYS_OUT, testFolder)

        when:
        collector.forEach(toSpecsMap);

        then:
        specs['spock.damagecontrol.TestResultsWithoutSysOut'].output.standard == ''
    }

    def 'should collect error output for spec'() {
        given:
        copyFileToDirectory(XML_WITH_SYS_OUT, testFolder)

        when:
        collector.forEach(toSpecsMap);

        then:
        specs['spock.damagecontrol.TestResultsWithSysOut'].output.error == 'error output message'
    }

    def 'should collect results without error output'() {
        given:
        copyFileToDirectory(XML_WITHOUT_SYS_OUT, testFolder)

        when:
        collector.forEach(toSpecsMap);

        then:
        specs['spock.damagecontrol.TestResultsWithoutSysOut'].output.error == ''
    }

    def 'should collect anything when result file has no test cases'() {
        given:
        copyFileToDirectory(XML_WITH_NO_TEST_CASE, testFolder)

        when:
        collector.forEach(toSpecsMap);

        then:
        specs.size() == 0
    }

    def 'should collect anything when result file is empty'() {
        given:
        copyFileToDirectory(EMPTY, testFolder)

        when:
        collector.forEach(toSpecsMap);

        then:
        specs.size() == 0
    }
}
