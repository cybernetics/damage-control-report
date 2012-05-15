package spock.damagecontrol

class HtmlSpecDefinitionFormatterTest extends BaseSpec {

    private static final String code = """package samples.definitions

/* Comments */
class SampleSpecTest extends Specification {

    def 'feature 1'() { /* F1 */
        given: 'given block'
        println 'given statement'

        when: 'when block'
        println 'when statement'

        then: 'then block'
        'some condition'
    }

    def "feature 2"() {
        given: "given block 2"
        println "given statement 2"

        when: "when block 2"
        println "when statement 2"

        then: "then block 2"
        "some condition 2"
    }
}
"""

    private Spec spec

    private SpecDefinition specDefinition = new SpecDefinition(code)

    private HtmlSpecDefinitionFormatter formatter

    def setup() {
        spec = new Spec('samples.definitions.SampleSpecTest')
        formatter = new HtmlSpecDefinitionFormatter(spec, specDefinition)
    }

    def 'should name HTML file based on spec name'() {
        when:
        File htmlFile = formatter.file(new File('.'))

        then:
        htmlFile.name == 'samples.definitions.SampleSpecTest.html'
    }

    def 'should surround spec definition with div'() {
        when:
        String html = formatter.format()

        then:
        html =~ /(?s)<div id='spec-definition'>.*(SampleSpecTest).*<\/div>/
    }

    def 'should identify "class" as reserved word'() {
        when:
        String html = formatter.format()

        then:
        html =~ /(?s).*<span class='reserved'>class<\/span>.*/
    }

    def 'should identify "package" as reserved word'() {
        when:
        String html = formatter.format()

        then:
        html =~ /(?s).*<span class='reserved'>package<\/span>.*/
    }

    def 'should identify "def" as reserved word'() {
        when:
        String html = formatter.format()

        then:
        html =~ /(?s).*<span class='reserved'>def<\/span>.*/
        html =~ /(?s).*samples\.definitions.*/
    }

    def 'should identify "extends" as reserved word'() {
        when:
        String html = formatter.format()

        then:
        html =~ /(?s).*<span class='reserved'>extends<\/span>.*/
    }

    def 'should identify single line comments'() {
        when:
        String html = formatter.format()

        then:
        html =~ /(?s).*<span class='comments'>\Q\/* Comments *\/\E<\/span>.*/
    }

    def 'should identify single quote string literals'() {
        when:
        String html = formatter.format()

        then:
        html =~ /(?s).*<span class='string-literal'>'feature 1'<\/span>.*/
    }

    def 'should identify double quote string literals'() {
        when:
        String html = formatter.format()

        then:
        html =~ /(?s).*<span class='string-literal'>"feature 2"<\/span>.*/
    }

    def 'should identify lines'() {
        when:
        String html = formatter.format()

        then:
        html =~ /(?s).*<span class='line-number'>1<\/span>.*package.* samples.*/
    }

    def 'should indicate line where error occurred'() {
        given:
        spec.features['feature 1'] = new Feature()
        spec.features['feature 1'].failed 'error message', 'at SampleSpecificationTest.shouldFail(SampleSpecTest.groovy:15)'

        when:
        String html = formatter.format()

        then:
        html =~ /(?m)<span class='error'>.*'some condition'.*<\/span>$/
    }
}
