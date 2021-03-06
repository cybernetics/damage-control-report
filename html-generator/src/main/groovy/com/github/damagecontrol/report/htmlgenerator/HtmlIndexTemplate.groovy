package com.github.damagecontrol.report.htmlgenerator

import groovy.text.GStringTemplateEngine

class HtmlIndexTemplate {

    private static final INDEX_URL = getResource('/com/github/damagecontrol/htmlreport/templates/index.html')
    private static final TEMPLATE = new GStringTemplateEngine().createTemplate(INDEX_URL)

    private final basePage = new BaseHtmlTemplate()

    def generate(title, results) {
        basePage.decorate(
            "Specifications - ${title}",
            TEMPLATE.make([
                title: title,
                featuresSummaryFragment: new HtmlFeaturesSummaryTemplate().generate(results.summary),
                results: results
            ])
        )
    }
}
