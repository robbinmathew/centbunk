/**
 * Created by pmathew on 1/15/16.
 */

function buildRateChangePanel(title, record) {
    var mainContent = Ext.create('Ext.form.Panel', {
        id:'changeRatePanel',
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
            xtype: 'gridpanel',
            id:'rateChangeGrid',
            autoscroll: true,
            height: 'auto',
            store: buildAvailableProductListStore("PRIMARY_PRODUCTS"),
            //forceFit: true,
            loadMask: false,
            viewConfig: {
                loadMask: true,
                markDirty:false,
                stripeRows: true
            },
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1
                })
            ],
            listeners : {
                edit: function (editor, e, eOpts) {
                    onRateUpdate();
                    return true;
                },
                afterlayout: function () {
                    onRateUpdate();
                }
            },
            columns:[{
                text: "Product",
                dataIndex: 'productName',
                flex: 2,
                sortable: true
            },{
                text: "Stock",
                dataIndex: 'currentStock',
                flex: 1,
                //align: 'right',
                sortable: true
            },{
                text: "Old price",
                dataIndex: 'unitSellingPrice',
                flex: 1,
                //align: 'right',
                sortable: true
            },{
                text: "New Price",
                dataIndex: 'newUnitSellingPrice',
                flex: 1,
                field: numberFieldConfig(),
                renderer: editableColumnRenderer,
                sortable: false
            },{
                text: "Rate difference",
                dataIndex: 'rateDiff',
                flex: 1,
                sortable: false
            },{
                text: "Total cash diff",
                dataIndex: 'cashDiff',
                flex: 1,
                sortable: false
            }]
        },{
            xtype:'fieldset',
            //defaults: {anchor: '100%',height:'30px'},
            layout: 'hbox',
            //title:'Opening/Closing',
            //width:'100%',
            defaultType: 'textfield',
            fieldDefaults: {
                msgTarget: 'side',
                labelSeparator : ' :',
                margins: '10 10 10 0',
                labelWidth: 100
            },
            items: [
                {
                    xtype: 'button',
                    text: 'Save',
                    flex:1,
                    margins: '10 5 5 200',
                    handler: function () {
                        onRateChangeSave();
                    }
                },{
                    xtype: 'button',
                    flex:1,
                    margins: '10 200 5 0',
                    text: 'Clear',
                    handler: function () {
                        updateSubPanel(record);

                    }
                }
            ]
        }]
    });
    return mainContent;
}

function onRateUpdate() {
    var store = Ext.getCmp('rateChangeGrid').store;
    store.each(function(record) {
        //Clear negative newUnitSellingPrice
        if(record.data.newUnitSellingPrice <=0) {
            record.set('newUnitSellingPrice', record.data.unitSellingPrice);
        }
        record.set('rateDiff', (record.data.newUnitSellingPrice - record.data.unitSellingPrice).toFixed(2));
        record.set('cashDiff', (record.data.rateDiff * record.data.currentStock).toFixed(2));

    });
}

function onRateChangeSave() {
    var errors = [];
    var warnings = [];
    var store = Ext.getCmp('rateChangeGrid').store;
    var changes = [];
    store.each(function(record) {
        if(Math.abs(record.data.rateDiff)> 5) {
            warnings.push("Rate difference of " + record.data.rateDiff + " for " + record.data.productName + " seems high");
        }

        if(Math.abs(record.data.rateDiff)> 0) {
            var obj = {
                productId:record.data.productId,
                newUnitPrice:record.data.newUnitSellingPrice,
                totalCashDiff:record.data.cashDiff,
                oldPrice:record.data.unitSellingPrice,
                currentStock:record.data.currentStock,
                margin:record.data.margin
            };
            changes.push(obj);
        }
    });

    if(changes.length == 0) {
        errors.push("No change found for the rates.")
    }

    checkErrorsWarningsAndProceed(errors, warnings, function() {
        var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Saving rate change..."});
        myMask.show();

        //Save
        Ext.Ajax.request({
            url: 'api/saveRateChange',
            method: 'POST',
            jsonData:changes,
            success: function() {
                myMask.hide();
                Ext.MessageBox.alert('Success', "Successfully saved the rate change.");
                updateSummaryPanel();
            },
            failure : function(response) {
                myMask.hide();
                Ext.Msg.alert("Error", "Failed to save. Please try again. <br/> " + response.responseText);
            }
        });
    });
    return true;

    var store = Ext.getCmp('rateChangeGrid').store;

    store.each(function(record) {
        //Clear negative receiptAmt
        if(record.data.newUnitSellingPrice <=0) {
            record.set('newUnitSellingPrice', record.data.unitSellingPrice);
        }
        record.set('rateDiff', (record.data.newUnitSellingPrice - record.data.unitSellingPrice).toFixed(2));
        record.set('cashDiff', (record.data.rateDiff * record.data.currentStock).toFixed(2));

    });
}
