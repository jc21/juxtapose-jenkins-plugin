package jenkins.plugins.juxtapose.GlobalConfig;

f = namespace('/lib/form')

f.section(title: _('Juxtapose Webhook Settings')) {
    f.entry(field: 'juxtaposeWebhookURL', title: _('Juxtapose Webhook URL Endpoint')) {
        f.textbox()
    }
}
