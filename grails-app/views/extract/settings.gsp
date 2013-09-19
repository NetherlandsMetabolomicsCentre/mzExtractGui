<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Extract</h2>

        <g:form controller="extract" action="settings" name="save_extraction_settings" method="POST">
            <g:field name="extractFolderKey" type="hidden" value="${params.extractFolderKey}" />
            <g:field name="dataFolderKey" type="hidden" value="${params.dataFolderKey}" />
            <table>
                <g:each in="${defaultSettings}" var="defaultSetting">
                    <tr>
                        <td style="padding-right:15px;"><div style="margin: 5px" class="hint hint--top hint--rounded" data-hint="${defaultSetting.help}"><i class="icon-info-sign"></i></div>${defaultSetting.label}</td>
                        <td>
                            <g:if test="${defaultSetting.type == 'select'}">
                                <g:select name="${defaultSetting.name}" optionKey="value" optionValue="label" from="${defaultSetting.options}" value="${existingSettings[defaultSetting.name]}" />
                            </g:if>
                            <g:else>
                                <g:field name="${defaultSetting.name}" type="text" value="${existingSettings[defaultSetting.name]}" />
                            </g:else>
                        </td>
                    </tr>
                </g:each>
            </table>
            <g:if test="${extractFolder.files['mat']?.size() >= 1}">
              <div class="alert alert-error">
                Unable to update the settings! Output exists based on these settings!
              </div>              
              <g:submitButton name="submit_save" disabled="disabled" value="Save" />
              <g:submitButton name="submit_back" value="Back" />
            </g:if>
            <g:else>
              <g:submitButton name="submit_save" value="Save" />
              <g:submitButton name="submit_next" value="Next" />
            </g:else>
        </g:form>
    </body>
</html>
