Ext.require('Ext.container.Viewport');

var bunkCache = {};

function getDataWithAjax(urlPath, success, failure) {
    Ext.Ajax.request({
        url: urlPath,
        success: success,
        failure: (failure? failure :function(form, action) {
            console.log('failed to get response for ' + urlPath);
        })
    });
}

var navStore = Ext.create('Ext.data.TreeStore', {
    root: {
        expanded: true,
        children: [
            {
                id:'daily-statement', text: "Daily statement", collapsible: false, leaf: true, panelbuilder: 'buildDailyStatementPanel'
            },{
                id:'products', text: "Products", expanded: true, collapsible: false,
                children: [
                    //{ id:'prods-add-edit', text: "Add/Edit", leaf: true},
                    {
                        id:'receive-prods',
                        text: "Receive products",
                        expanded: true,
                        collapsible: false,
                        children: [
                            { id:'prods-receive-petrol-diesel', text: "Petrol/Diesel", leaf: true, panelbuilder:'buildFuelReceiptsPanel'},
                            { id:'prods-receive-lubes', text: "Lubes", leaf: true, panelbuilder:'buildLubesReceiptsPanel' },
                            { id:'prods-receive-others', text: "Battery water/Others", leaf: true, panelbuilder: 'buildBatteryWaterReceiptsPanel' }
                        ]
                    }
                ]
            }/*,{
                id:'parties', text: "Parties", expanded: true, collapsible: false, children: [
                    { id:'parties-add-edit', text: "Add/Edit", leaf: true }
                ]
            }*/
        ]
    }
});

Ext.application({
    name: 'bronz.bunk',
    extend: 'Ext.app.Application',
    launch: function() {
        Ext.getBody().setStyle('overflow', 'auto');
        var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading..."});
        myMask.show();
        getDataWithAjax('api/info', function(response){
            bunkCache.infos = Ext.decode(response.responseText);
            if(Ext.getCmp('userInfo')) {
                Ext.getCmp('userInfo').body.update('<h4>Username:' + username + '<br/>Date:' + (bunkCache.infos?bunkCache.infos.todayDateDayText:'-') + '</h4>');
            }
            myMask.hide();
        },function(response){
            console.log('failed to read info from server:' + response);
            myMask.hide();
            var failedMask = new Ext.LoadMask(Ext.getBody(), {msg:"Failed to read data from server. Please try after sometime."});
            failedMask.show();
        });
        Ext.create('Ext.container.Viewport', {
            layout:'border',
            defaults: {
               bodyStyle: 'padding:0px'
            },
            items: [{
                //title: 'Header',
                region:'north',
                header:false,
                margins: '0 0 0 0',
                cmargins: '0 0 0 0',
                height: 75,
                minSize: 75,
                maxSize: 75,
                resize:false,
                border: 0,
                layout:'border',
                items: [{
                    //title: 'Title',
                    preventBodyReset: true,
                    html: '<h1>Century bunk mgmt software</h1>',
                    width:'80%',
                    region:'west',
                    height:'100%',
                    flex:2,
                    margins: '0 0 0 0',
                    cmargins: '0 0 0 0'

                },{
                    id:'userInfo',
                    html: '<h4>Username:' + username + '<br/>Loading date</h4>',
                    preventBodyReset: true,
                    width:'20%',
                    region:'east',
                    height:'100%',
                    margins: '0 0 0 0',
                    cmargins: '0 0 0 0'
                }]
            },{
                id: 'maincontent',
                html:'Welcome',
                region:'center',
                layout:'vbox',
                margins: '0 0 0 0',
                width:'100%',
                height:'100%',
                autoScroll:true,
                items : [buildSummaryPanel()]
            },{
                //title: 'NavPanel',
                header:false,
                region:'west',
                margins: '0 0 0 0',
                cmargins: '0 0 0 0',
                padding: '0px 0px 0px 0px',
                width: 200,
                minWidth: 150,
                maxWidth: 250,
                resize:true,
                xtype: 'treepanel',
                rootVisible: false,
                // Sharing the store synchronizes the views:
                store: navStore,
                lines : false,
                collapsible: false,
                useArrows: true,
                listeners: {
                    beforecollapse: function(n) {
                        return false;
                    },
                    itemclick: function(view, record, item, index, evt, eOpts) {
                        if (!record.isLeaf()) {
                            return;
                        }
                        updateSubPanel(record);
                        return false;
                    }
                }
            }]
        });
    }
});

function updateSubPanel(record) {
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading..."});
    myMask.show();
    var menuId = record.get('id');
    var newPanel;
    if(record.raw.panelbuilder) {
        newPanel = window[record.raw.panelbuilder](getTitle(record), record);
    } else {
        newPanel = new Ext.Panel({
            html: 'Success' + menuId
        });
    }
    var mainPanel = Ext.getCmp('maincontent');
    mainPanel.update('');//Clear current contents
    mainPanel.removeAll(); //remove all the items

    mainPanel.add(newPanel);
    mainPanel.doLayout();
    newPanel.doLayout();
    myMask.hide();
}

function updateSummaryPanel() {
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading..."});
    myMask.show();
    var mainPanel = Ext.getCmp('maincontent');
    mainPanel.update('');//Clear current contents
    mainPanel.removeAll(); //remove all the items

    mainPanel.add(buildSummaryPanel());
    mainPanel.doLayout();
    myMask.hide();
}

function buildSummaryPanel() {
    var summaryPanel = Ext.create('Ext.form.Panel', {
        id:'summaryPanel',
        title: "Summary",
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
        html:'<h4>Summary</h4>'
    });
    return summaryPanel;
}

function getTitle(record) {
    var node = record;
    var title = '';
    do {
        if(title.length > 0) {
            title = node.get('text') + ' -> ' + title;
        } else {
            title = node.get('text');
        }
        node = node.parentNode;
    } while (node.parentNode)
    return title;
}

function prepareErrorMsg(msg, errors) {
    var message = '<b>' + msg + '</b><br/><ul style="list-style-type:square">';
    errors.forEach(function(item){
        message = message + '<li>' + item + '</li>';
    });
    message = message + '</ul>';
    return message;
}

function editableColumnRenderer(value, metaData, record, rowIndex, colIndex, store, view) {
    metaData.style = 'background:#ffffe5 !important;';
    return value;
}

function isNumber (o) {
    return ! isNaN (o-0) && o !== null && o !== "" && o !== false;
}


