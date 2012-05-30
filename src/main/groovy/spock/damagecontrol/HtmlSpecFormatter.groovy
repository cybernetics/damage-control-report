package spock.damagecontrol

import groovy.text.GStringTemplateEngine
import groovy.text.Template

class HtmlSpecFormatter {

    private static final URL SPEC_HTML_URL = HtmlSpecFormatter.class.getResource('/spock/damagecontrol/templates/spec.html')

    private final Spec spec
    private final SpecDefinition specDefinition
    private final Template specHtmlTemplate

    HtmlSpecFormatter(spec, specDefinition) {
        this.spec = spec
        this.specDefinition = specDefinition
        this.specHtmlTemplate = new GStringTemplateEngine().createTemplate(SPEC_HTML_URL)
    }

    def file(baseFolder) {
        return new File(baseFolder.absolutePath + '/' + spec.name + '.html')
    }

    def format() {
        String result = specDefinition.sourceCode

        SourceCodeNormalizer stringLiteral = new SourceCodeNormalizer(/('.*')|(".*")/, 'string_literal')
        SourceCodeNormalizer comments = new SourceCodeNormalizer(/\/\*(.*)\*\//, 'comments')

        result = stringLiteral.normalize(result)
        result = comments.normalize(result)

        result = reservedWords(result)
        result = lineNumbers(result)
        result = errorLines(result)

        result = stringLiteral.denormalize(result, { "<span class='string-literal'>${it}</span>" })
        result = comments.denormalize(result, { "<span class='comments'>${it}</span>" })

        return specHtmlTemplate.make([
                spec_definition: result,
                spec_standard_output: spec.output.standard,
                spec_error_output: spec.output.error,
                features: spec.features
        ]).toString()
    }

    private String lineNumbers(String result) {
        int lineCount = 1
        return result.replaceAll(/(?m)^.*$/, { "<span class='line-number'>${lineCount++}</span>${it}" })
    }

    private String errorLines(String result) {
        List errorLines = spec.errorLines()
        int lineCount = 1
        return result.replaceAll(/(?m)^.*$/, { errorLines.contains(lineCount++) ? "<span class='error'>${it}</span>" : it })
    }

    private String reservedWords(String result) {
        return result.replaceAll(/((package)|(class)|(extends)|(def))[\s]+/, {
            "<span class='reserved'>${it[1]}</span> "
        })
    }
}
