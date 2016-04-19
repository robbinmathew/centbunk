/**
 * Created by pmathew on 4/18/16.
 */

function buildReportsPanel(title, record) {
    var maxDate = parseDateText(bunkCache.infos.todayDateText);
    //Previous day
    maxDate.setDate(maxDate.getDate()-1);
    var minDate = parseDateText(bunkCache.infos.firstDateText);
    var mainContent = Ext.create('Ext.form.Panel', {
        id:'reportsPanel',
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
            //defaults: {anchor: '100%',height:'30px'},
            layout: 'vbox',
            title:'Select report type',
            //width:'100%',
            defaultType: 'textfield',
            fieldDefaults: {
                msgTarget: 'side',
                labelSeparator : ' :',
                margins: '5 10 5 50',
                labelWidth: 0
            },
            items: [
                {
                    xtype      : 'fieldcontainer',
                    fieldLabel : '',
                    defaultType: 'radiofield',
                    defaults: {
                        flex: 1
                    },
                    layout: 'vbox',
                    items: [
                        {
                            boxLabel  : 'Daily Statement',
                            name      : 'size',
                            checked:true,
                            inputValue: 'm',
                            id        : 'radio1'
                        }/*, {
                            boxLabel  : 'Other',
                            name      : 'size',
                            inputValue: 'l',
                            id        : 'radio2'
                        }*/
                    ]
                },{
                    xtype: 'label',
                    margins: '5 10 5 100',
                    text: 'Date'
                },
                {
                    xtype: 'datepicker',
                    id: 'startDate',
                    maxDate: maxDate,
                    minDate: minDate,
                    value: maxDate,
                    margins: '5 10 5 100',
                    showToday : false
                }, {
                    xtype: 'button',
                    text: 'Show report',
                    flex:1,
                    margins: '10 5 5 100',
                    handler: function () {
                        onShowReport();
                    }
                }
            ]
        }]
    });
    return mainContent;
}

function onShowReport() {
    var selectedDate = Ext.getCmp('startDate').getValue();

    var url = '/api/report?type=DAILY_STMT&dateText=' + dateToSimpleText(selectedDate);

    //url
    var popUp = Ext.create('Ext.window.Window', {
        //header:false,
        height: 750,
        modal:true,
        width: 1000,
        layout: 'vbox',
        anchor:"100% 95%",
        title:'Report',
        maximizable: false,
        minimizable: false
    });
    popUp.add({html: '<iframe height="670", width="995" src="'+ url +'"></iframe>'});
    popUp.add({
        xtype: 'button',
        text: 'Close Window',
        width: '100%',
        height: 30,
        cls: 'close-button',
        handler: function(){
            var win = Ext.WindowManager.getActive();
            if (win) {
                win.close();
            }
        }
    });
    popUp.show();
}