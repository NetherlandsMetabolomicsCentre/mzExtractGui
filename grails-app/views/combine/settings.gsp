<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Combine</h2>

        <g:form controller="combine" action="settings" name="save_combine_settings" method="POST">
            <g:field name="combineFolderKey" type="hidden" value="${params.combineFolderKey}" />
            <g:field name="alignFolderKey" type="hidden" value="${params.alignFolderKey}" />
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
            <g:submitButton name="submit_save" value="Save" />
            <g:submitButton name="submit_next" value="Next" />
        </g:form>
    </body>
</html>
