Ext.require('Ext.container.Viewport');

var navStore = Ext.create('Ext.data.TreeStore', {
    root: {
        expanded: true,
        children: [
            {
                id:'daily-statement',
                text: "Daily statement",
                collapsible: false,
                leaf: true
            },
            {
                id:'products',
                text: "Products",
                expanded: true,
                collapsible: false,
                children: [
                    { id:'prods-add-edit', text: "Add/Edit", leaf: true},
                    {
                        id:'receive-prods',
                        text: "Receive products",
                        expanded: true,
                        collapsible: false,
                        children: [
                            { id:'prods-receive-petrol-diesel', text: "Petrol/Diesel", leaf: true, panelbuilder:'buildPetrolReceiptsPanel'},
                            { id:'prods-receive-lubes', text: "Lubes", leaf: true },
                            { id:'prods-receive-others', text: "Battery water/Others", leaf: true }
                        ]
                    }
                ]
            },
            {
                id:'parties',
                text: "Parties",
                expanded: true,
                collapsible: false,
                children: [
                    { id:'parties-add-edit', text: "Add/Edit", leaf: true }
                ]
            }
        ]
    }
});

Ext.application({
    name: 'centbunk',
    extend: 'Ext.app.Application',
    launch: function() {
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
                layout:'hbox',
                items: [{
                    //title: 'Title',
                    preventBodyReset: true,
                    html: '<h1>Century bunk mgmt software</h1>',
                    width:'100%',
                    height:'100%',
                    margins: '0 0 0 0',
                    cmargins: '0 0 0 0'

                }/*,{
                    //title: 'Header',
                    header:false,
                    margins: '0 0 0 0',
                    cmargins: '0 0 0 0',
                    height: 50,
                    minSize: 50,
                    maxSize: 50,
                    resize:false
                }*/]
            },{
                id: 'maincontent',
                html:'Welcome',
                region:'center',
                margins: '0 0 0 0',
                width:'100%',
                height:'100%'
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
                    itemclick: function(view, record, item, index, evt, eOpts) {
                        if (!record.isLeaf()) {
                            return;
                        }
                        var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading..."});
                        myMask.show();
                        var menuId = record.get('id');
                        var newPanel;
                        if(record.raw.panelbuilder) {
                            newPanel = window[record.raw.panelbuilder]();

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
                }
            }]
        });
    }
});

function buildPetrolReceiptsPanel(record) {
    var mainContent = Ext.create('Ext.form.Panel', {
        //url:'save-form.php',
        frame:true,
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
            fieldLabel: 'Invoice Number',
            name: 'invNo',
            allowBlank:false
        },{
            fieldLabel: 'Total amount',
            name: 'amt',
            editable:false
        }],

        buttons: [{
            text: 'Save'
        },{
            text: 'Cancel'
        }]
    });
   return mainContent;
};