# Juxtapose plugin for Jenkins

Provides Jenkins notification with [Juxtapose](https://github.com/jc21/juxtapose) installations.


## Install Instructions

1. Download the latest release hpi file
2. Configure your Jenkins installation, Manage Plugins
3. Install this plugin on your Jenkins server using the Advanced tab
4. Configure Jenkings Global settings to add your Juxtapose endpoint URL

From here you can add to your Job's post build actions or use the pipeline steps below.

Technically this plugin could be used with any http endpoint that accepts a JSON POST payload.


## Jenkins Pipeline Support

Includes [Jenkins Pipeline](https://github.com/jenkinsci/workflow-plugin)
support:

```
juxtapose event: 'success'
juxtapose event: 'failure'
juxtapose event: 'aborted'
juxtapose event: 'unstable'
juxtapose event: 'regression'
juxtapose event: 'changed'
juxtapose event: 'fixed'
```

Additionally you can pass a endpoint url as part of the step:

```
juxtapose event: 'success', url: 'https://your-juxtapose....'
```
