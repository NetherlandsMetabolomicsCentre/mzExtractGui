<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Extraction</h2>

        <g:form controller="extract" action="settings" name="save_extraction_settings" method="POST">
            <g:field name="extractionFolderKey" type="hidden" value="${params.extractionFolderKey}" />
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
            <g:submitButton name="submit_save" value="Save" />
            <g:submitButton name="submit_next" value="Next" />
        </g:form>
    </body>
</html>
