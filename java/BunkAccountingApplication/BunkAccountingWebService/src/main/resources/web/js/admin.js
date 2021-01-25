/**
 * Created by pmathew on 5/12/17.
 */


function showAdminPanel(title, record) {

    var mainContent = Ext.create('Ext.form.Panel', {
        id:'adminPanel',
        title: title,
        frame:true,
        autoScroll:true,
        bodyStyle:'padding:0px 0px 0',
        width: '100%',
        fieldDefaults: {
            msgTarget: 'side',
            labelWidth: 75
        },
        defaultType: 'textfield',
        defaults: {
            anchor: '100%'
        },
        items: [{
            xtype:'fieldset',
            width: '100%',
            layout: {
                type: 'table',
                width: '100%',
                columns: 4
            },
            defaultType: 'textfield',
            fieldDefaults: {
                msgTarget: 'side',
                labelSeparator : ' :',
                margins: '10 10 10 0',
                labelWidth: 100
            },
            items: [
                {
                        xtype: 'numberfield',
                        anchor: '100%',
                        name: 'fromDays',
                        fieldLabel: 'From',
                        value: 30,
                        minValue: 5,
                        rowspan:1,
                        colspan:2
                },{
                      xtype: 'numberfield',
                      anchor: '100%',
                      name: 'toDays',
                      fieldLabel: 'To',
                      value: 0,
                      minValue: 0,
                      rowspan:1,
                      colspan:2
                },{
                      xtype     : 'textareafield',
                      grow      : true,
                      name      : 'currentData',
                      fieldLabel: 'Current Data',
                      rowspan:1,
                     // height: '300',
                      width: '100%',
                      labelWidth: '50',
                      hideLabel : true,
                      colspan:4,
                      anchor    : '100%'
                },{
                      xtype     : 'textareafield',
                      grow      : true,
                      name      : 'newData',
                      fieldLabel: 'New Data',
                      rowspan:2,
                      colspan:1,
                      anchor    : '100%'
                },{
                    xtype: 'button',
                    rowspan:1,
                    colspan:1,
                    margins: '10 200 5 0',
                    text: 'Load transactions',
                    handler: function () {


                    }
                }
            ]
        }]
    });
    return mainContent;
}