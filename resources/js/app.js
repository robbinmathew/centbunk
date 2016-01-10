Ext.require('Ext.container.Viewport');
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
                title: 'Footer',
                region: 'south',
                height: 150,
                minSize: 75,
                maxSize: 250,
                cmargins: '0 0 0 0'
            },{
                //title: 'Header',
                region:'north',
                header:false,
                margins: '0 0 0 0',
                cmargins: '0 0 0 0',
                height: 50,
                minSize: 50,
                maxSize: 50,
                resize:false,
                layout:'hbox',
                items: [{
                    //title: 'Title',
                    region: 'west',
                    preventBodyReset: true,
                    html: '<h3>Century bunk mgmt software</h3>',
                    margins: '0 0 0 0',
                    cmargins: '0 0 0 0'

                },{
                    //title: 'Header',
                    region:'north',
                    header:false,
                    // margins: '5 0 0 0',
                    // cmargins: '5 5 0 0',
                    height: 50,
                    minSize: 50,
                    maxSize: 50,
                    resize:false,
                    layout:'border'
                }]
            },{
                title: 'Main Content',
                region:'center',
                margins: '5 0 0 0'
            }]
        });
    }
});