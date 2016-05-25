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

    var url = '/api/report?type=DAILY_STMT&format=image&dateText=' + dateToSimpleText(selectedDate);
    var reportWindowHeight = Ext.getBody().getViewSize().height-100;
    var reportWindowWidth = Ext.getBody().getViewSize().width-100;

    var image = Ext.create('Ext.Img', {
        src: url,
        listeners : {
            load : {
                element : 'el',  //the rendered img element
                fn : setimg
            }
        }
    });

    function setimg() {
        if(reportWindowWidth > image.getEl().dom.naturalWidth) {
            image.getEl().dom.height = image.getEl().dom.naturalHeight;
            image.getEl().dom.width = image.getEl().dom.naturalWidth;
            var window = Ext.getCmp('report-window');
            window.setWidth(image.getEl().dom.naturalWidth);
            window.doLayout();
            window.center();
        } else {
            var minimumWidth = (reportWindowWidth<560) ? 560: reportWindowWidth;
            var ratio = minimumWidth/image.getEl().dom.naturalWidth;
            image.getEl().dom.height = image.getEl().dom.naturalHeight * ratio;
            image.getEl().dom.width = image.getEl().dom.naturalWidth * ratio;
        }
    }

    //url
    var popUp = Ext.create('Ext.window.Window', {
        //header:false,
        height: reportWindowHeight,
        width:  reportWindowWidth,
        modal:true,
        id:'report-window',
        layout: 'vbox',
        anchor:"100% 95%",
        //autoScroll:true,
        title:'Report',
        maximizable: false,
        minimizable: false,
        items:[{
            //layout:'fit',
            margins: '0 0 0 0',
            width:'100%',
            height:(reportWindowHeight - 65),
            autoScroll:true,
            items : [image]
        },{
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
        }]
    });
    //popUp.add({html: '<iframe height="' + (reportWindowHeight - 65) + '", width="' + (reportWindowWidth - 5) + '" src="'+ url +'"></iframe>'});
    //popUp.add({html:'<img src="'+ url +'" height="' + (reportWindowHeight - 65) + '", width="' + (reportWindowWidth - 5) + '" />'});
    /*popUp.add({
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
    });*/
    popUp.show();
}