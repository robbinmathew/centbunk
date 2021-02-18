var savedDailyStmt = {};
var partyComboStoreLoaded = false;
var empComboStoreLoaded = false;
var oilComboStoreLoaded = false;
var loadedSavedTransactions = false;
var loadingSavedTransactions = false;
var dataLoaded = false;
var dailyStatementLoadMask;
var lastSavedDate;
var lastEditDate;
var autoSaveTimeout;
var autoUpdateSavedDate;
var autoSaveFailureCount = 0;


function buildDailyStatementPanel(title, record) {
    //Reset all values
    savedDailyStmt = {};
    loadingSavedTransactions = false;
    partyComboStoreLoaded = false;
    empComboStoreLoaded = false;
    oilComboStoreLoaded = false;
    loadedSavedTransactions = false;
    lastEditDate = null;
    dataLoaded = false;
    lastSavedDate = null;
    autoSaveFailureCount = 0;
    if(autoSaveTimeout) {
        //Clear the last one if one exist
        clearTimeout(autoSaveTimeout);
    }
    autoSaveTimeout = setTimeout(autoSave, 60000);

    autoUpdateSavedDate = setInterval(updateSavedTime, 1000);

    dailyStatementLoadMask = new Ext.LoadMask( Ext.getBody(), {
            msg:"Preparing for daily statement...",
            listeners: {
                beforehide: function (loadMask, eOpts) {
                    console.log("Test");
                    return loadMask.hideMask(loadMask, eOpts);
                }
            }
        });

    dailyStatementLoadMask.show();

    var oilProdStore = buildAvailableProductListStore('SECONDARY_PRODUCTS');

    oilProdStore.on('load',function (store, records, options){
        oilComboStoreLoaded = true;
        getSavedTransactions();
    });

    var oilProdCombo = new Ext.form.ComboBox({
        store: oilProdStore,
        valueField: "productId",
        id:'lubeCombo',
        editable: false,
        lazyInit: false,
        // Template for the dropdown menu.
        // Note the use of the "x-list-plain" and "x-boundlist-item" class,
        // this is required to make the items selectable.
        tpl: Ext.create('Ext.XTemplate',
            '<ul class="x-list-plain"><tpl for=".">',
            '<li role="option" class="x-boundlist-item">{productName} - {unitSellingPrice}Rs</li>',
            '</tpl></ul>'
        ),
        // template for the content inside text field
        displayTpl: Ext.create('Ext.XTemplate',
            '<tpl for=".">',
                '<tpl if="{productName}.length &gt; 0">',
                    '{productName} - {unitSellingPrice}Rs',
                '</tpl>',
            '</tpl>'
        ),
        listeners: {
            focus: {
                fn : function(comboField) {
                    comboField.expand();
                }
            },
            select: {
                fn: function(c, r, eopts) {
                    c.ownerCt.completeEdit();
                    var gridRecord = c.ownerCt.floatParent.getSelectionModel().getSelection()[0];
                    var comboRecord = r[0];
                    //Copy values to grid record
                    gridRecord.set("productName", comboRecord.data.productName + ' - ' + comboRecord.data.unitSellingPrice +'Rs');
                    gridRecord.set("currentStock", comboRecord.data.currentStock);
                    gridRecord.set("productId", comboRecord.data.productId);
                    gridRecord.set("unitSellingPrice", comboRecord.data.unitSellingPrice);
                    gridRecord.set("actualSale", 1);

                    onOilSaleUpdate();
//currentStock
                    var oilGridStore = Ext.getCmp('oilSaleGrid').store;

                    var rowNo = oilGridStore.indexOf(gridRecord);
                    //Find last used row
                    var lastUsedRow = -1;
                    oilGridStore.each(function(record, indx) {
                        if(record.data.productName.length > 0 && record !== gridRecord) {
                            lastUsedRow = indx;
                        }
                    });

                    if(rowNo > lastUsedRow + 1) {
                        oilGridStore.remove(gridRecord);
                        oilGridStore.insert(lastUsedRow+1, gridRecord);
                    }

                    if(rowNo >= oilGridStore.getCount() -1) {
                        for (var i=0; i<5;i++) {
                            oilGridStore.add(new LubeSale());
                        }
                    }
                    gridRecord.set("id", comboRecord.data.productId + ":" + (lastUsedRow+1));
                }
            }
        }
    });
    var partyStore = buildPartyListStore('ALL_ACTIVE_NON_EMPLOYEE_PARTIES');

    partyStore.on('load',function (store, records, options){
        partyComboStoreLoaded = true;
        notifyAndRemoveStalePartyTransactions();
        getSavedTransactions();
    });

    var partyCombo = new Ext.form.ComboBox({
        id:'partyCombo',
        store: partyStore,
        valueField: "id",
        displayField: "name",
        editable: false,
        listeners: {
            focus: {
                fn : function(comboField) {
                    comboField.expand();
                }
            },
            select: {
                fn: function(c, r, eopts) {
                    c.ownerCt.completeEdit();
                    var gridRecord = c.ownerCt.floatParent.getSelectionModel().getSelection()[0];
                    var comboRecord = r[0];
                    //Copy values to grid record
                    gridRecord.set("partyName", comboRecord.data.name);
                    gridRecord.set("partyId", comboRecord.data.id);
                    gridRecord.set("balance", comboRecord.data.balance);
//currentStock
                    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;

                    var rowNo = partyTransGridStore.indexOf(gridRecord);
                    //Find last used row
                    var lastUsedRow = -1;
                    partyTransGridStore.each(function(record, indx) {
                        if(record.data.partyName.length > 0 && record !== gridRecord) {
                            lastUsedRow = indx;
                        }
                    });

                    if(rowNo > lastUsedRow + 1) {
                        partyTransGridStore.remove(gridRecord);
                        partyTransGridStore.insert(lastUsedRow+1, gridRecord);
                    }

                    if(rowNo >= partyTransGridStore.getCount() -1) {
                        for (var i=0; i<10;i++) {
                            partyTransGridStore.insert(rowNo+1, new PartyTransaction());
                        }
                    }
                    gridRecord.set("id", comboRecord.data.id + ":" + (lastUsedRow+1));
                }
            }
        }
    });
    var empStore = buildEmployeeListStore();

    empStore.on('load',function (store, records, options){
        empComboStoreLoaded = true;
        notifyAndRemoveStaleEmpTransactions();
        getSavedTransactions();
    });

    var employeeCombo = new Ext.form.ComboBox({
        store: empStore,
        id:'empCombo',
        valueField: "id",
        displayField: "name",
        editable: false,
        listeners: {
            focus: {
                fn : function(comboField) {
                    comboField.expand();
                }
            },
            select: {
                fn: function(c, r, eopts) {
                    c.ownerCt.completeEdit();
                    var gridRecord = c.ownerCt.floatParent.getSelectionModel().getSelection()[0];
                    var comboRecord = r[0];
                    //Copy values to grid record
                    gridRecord.set("partyName", comboRecord.data.name);
                    gridRecord.set("partyId", comboRecord.data.id);
//currentStock
                    var employeeTransGridStore = Ext.getCmp('empTransGrid').store;

                    var rowNo = employeeTransGridStore.indexOf(gridRecord);
                    //Find last used row
                    var lastUsedRow = -1;
                    employeeTransGridStore.each(function(record, indx) {
                        if(record.data.partyName.length > 0 && record !== gridRecord) {
                            lastUsedRow = indx;
                        }
                    });

                    if(rowNo > lastUsedRow + 1) {
                        employeeTransGridStore.remove(gridRecord);
                        employeeTransGridStore.insert(lastUsedRow+1, gridRecord);
                    }

                    if(rowNo >= employeeTransGridStore.getCount() -1) {
                        for (var i=0; i<5;i++) {
                            employeeTransGridStore.insert(rowNo+1, new EmployeeTransaction());
                        }
                    }
                    gridRecord.set("id", comboRecord.data.id + ":" + (lastUsedRow+1));
                }
            }
        }
    });

    var mainContent = Ext.create('Ext.form.Panel', {
        id:'dailyStmtPanel',
        title: title,
        header: {
            // if you want your button positioned on the right hand side add
            titlePosition: 0,
            items: [
                {
                xtype: 'label',
                readOnly: true,
                id:'ds-notification',
                text: ''
            }]
        },

        //frame:true,
        bodyStyle:'padding:0px 0px 0',
        width: '100%',
        fieldDefaults: {
            msgTarget: 'side',
            labelWidth: 75
        },
        layout: 'border',
        defaultType: 'textfield',
        defaults: {
            anchor: '100%'
        },
        items: [{
            xtype:'fieldset',
            //defaults: {anchor: '100%',height:'30px'},
            layout: 'hbox',
            //title:'Opening/Closing',
            //width:'100%',
            //height:'50px',
            region:"north",
            defaultType: 'textfield',
            fieldDefaults: {
                msgTarget: 'side',
                labelSeparator : ' :',
                margins: '10 10 0 0',
                labelWidth: 100
            },
            items: [
                {
                    id:'openBal-field',
                    fieldLabel: 'Opening balance',
                    name: 'opBal',
                    flex:2,
                    readOnly: true
                },{
                    id:'clBal-field',
                    fieldLabel: 'Closing balance',
                    flex:2,
                    readOnly: true
                },{
                    xtype: 'button',
                    text: 'Save',
                    flex:1,
                    margins: '10 5 5 0',
                    disabled: false,
                    handler: function () {
                        onDailyStmtSave();
                    }
                },{
                    xtype: 'button',
                    flex:1,
                    margins: '10 5 5 0',
                    text: 'Submit',
                    handler: function () {
                        onDailyStmtSubmit();
                    }
                },{
                    xtype: 'button',
                    flex:1,
                    margins: '10 5 5 0',
                    text: 'Clear',
                    handler: function () {
                        deleteSavedStmt(record);
                    }
                }
            ]
        },{
            xtype: 'panel',
            autoscroll: true,
            region:"center",
            overflowY: 'scroll',
            items:[
                {
                    xtype: 'gridpanel',
                    id:'officeCashGrid',
                    autoscroll: true,
                    height: 'auto',
                    store: buildOfficeCashStore(),
                    forceFit: true,
                    loadMask: false,
                    viewConfig: {
                        loadMask: false,
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
                            onOfficeCashUpdate();
                            return true;
                        },
                        afterlayout: function () {
                            var clBalText = Ext.getCmp('clBal-field').getValue();
                            if(clBalText == "" || isNaN(clBalText)) {
                                //First load
                                onOfficeCashUpdate();
                            }
                        }
                    },
                    columns:[{
                        text: "Name",
                        dataIndex: 'name',
                        flex: 1
                    },{
                        text: "Opening",
                        dataIndex: 'opBal',
                        flex: 1
                    },{
                        text: "To office",
                        dataIndex: 'toOffice',
                        flex: 1,
                        sortable: false,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer
                    },{
                        text: "Paid to bank",
                        dataIndex: 'paidToBank',
                        flex: 1,
                        sortable: false,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer
                    },{
                        text: "Closing",
                        dataIndex: 'clBal',
                        flex: 1,
                        sortable: false
                    }]
                },{
                    xtype: 'gridpanel',
                    id:'meterSaleGrid',
                    height: 'auto',
                    store: buildActiveMeterListStore(),
                    forceFit: true,
                    loadMask: false,
                    viewConfig: {
                        loadMask: false,
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
                            onMeterSaleUpdate();
                        }
                    },
                    columns:[{
                        text: "Meter",
                        dataIndex: 'meterName',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Opening reading",
                        dataIndex: 'finalReading',
                        flex: 2,
                        //align: 'right',
                        sortable: false
                    },{
                        text: "Closing reading",
                        dataIndex: 'closingReading',
                        flex: 2,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer,
                        sortable: false
                    },{
                        text: "Total sale",
                        dataIndex: 'totalSale',
                        flex: 2,
                        sortable: false
                    },{
                        text: "Test sale in liter",
                        dataIndex: 'testSale',
                        flex: 1,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer,
                        sortable: false
                    },{
                        text: "Actual sale",
                        dataIndex: 'actualSale',
                        flex: 2,
                        sortable: false
                    }]
                },{
                    xtype: 'gridpanel',
                    id:'tankSaleGrid',
                    height: 'auto',
                    store: buildTankReceiptStore(bunkCache.infos.todayDate),
                    forceFit: true,
                    viewConfig: {
                        loadMask: false,
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
                            onMeterSaleUpdate();
                        }
                    },
                    columns:[{
                        text: "Tank name",
                        dataIndex: 'tankName',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Product",
                        dataIndex: 'productType',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Opening stock",
                        dataIndex: 'closingStock',
                        flex: 1,
                        //align: 'right',
                        sortable: false
                    },{
                        text: "Sale",
                        dataIndex: 'sale',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Stock after sale",
                        dataIndex: 'stockAfterSale',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Test",
                        dataIndex: 'testSale',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Dip stock",
                        dataIndex: 'dipStock',
                        flex: 1,
                        sortable: false,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer
                    },{
                        text: "Diff today",
                        dataIndex: 'diffToday',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Diff this month",
                        dataIndex: 'newDiffThisMonth',
                        flex: 1,
                        sortable: false
                    }]
                },{
                    xtype: 'gridpanel',
                    id:'fuelSaleGrid',
                    height: 'auto',
                    store: buildAvailableProductListStore('FUEL_PRODUCTS'),
                    forceFit: true,
                    viewConfig: {
                        loadMask: false,
                        markDirty:false,
                        stripeRows: true
                    },
                    columns:[{
                        text: "Product",
                        dataIndex: 'productName',
                        flex: 2,
                        sortable: false
                    },{
                        text: "Price",
                        dataIndex: 'unitSellingPrice',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Total sale",
                        dataIndex: 'totalSale',
                        flex: 1,
                        //align: 'right',
                        sortable: false
                    },{
                        text: "Total sale amt",
                        dataIndex: 'totalSaleAmt',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Test sale",
                        dataIndex: 'testSale',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Test sale amt",
                        dataIndex: 'testSaleAmt',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Actual sale",
                        dataIndex: 'actualSale',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Actual sale amt",
                        dataIndex: 'actualSaleAmt',
                        flex: 1,
                        sortable: false
                    }]
                },{
                    xtype: 'gridpanel',
                    id:'oilSaleGrid',
                    height: 'auto',
                    store: buildEmptyProductSaleStore(),
                    forceFit: true,
                    viewConfig: {
                        loadMask: false,
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
                            onOilSaleUpdate();
                        }
                    },
                    columns:[{
                        text: "Product",
                        dataIndex: 'productName',
                        flex: 2,
                        sortable: false,
                        editor: oilProdCombo,
                        renderer: editableColumnRenderer
                    },{
                        text: "Price",
                        dataIndex: 'unitSellingPrice',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Stock",
                        dataIndex: 'currentStock',
                        flex: 1,
                        //align: 'right',
                        sortable: false
                    },{
                        text: "Total sale",
                        dataIndex: 'actualSale',
                        flex: 1,
                        sortable: false,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer
                    },{
                        text: "Discount per unit",
                        dataIndex: 'discountPerUnit',
                        flex: 1,
                        sortable: false,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer
                    },{
                        text: "Amount after discount",
                        dataIndex: 'totalSaleAmt',
                        flex: 1,
                        sortable: false
                    },getRowActionsColumns("LubeSale", updateClosingBalance )]
                },{
                    xtype: 'gridpanel',
                    id:'partyTransGrid',
                    height: 'auto',
                    store: buildEmptyPartyTransactionStore(),
                    forceFit: true,
                    viewConfig: {
                        loadMask: false,
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
                            onPartyTransUpdate();
                        },
                        aftercellclick: function(iView, iCellEl, iColIdx, iRecord, iRowEl, iRowIdx, iEvent) {
                            var fieldName = iView.getGridColumns()[iColIdx].dataIndex;
                            if(fieldName === 'partyName') {
                                partyCombo.onTriggerClick();
                            }
                            return false;
                        },
                        beforeedit: function(iView, eventDetails) {
                            //Disable the edit of saved transactions
                            if(eventDetails.record.data.notEditable == true) {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    },
                    columns:[{
                        text: "Party name",
                        dataIndex: 'partyName',
                        flex: 2,
                        sortable: false,
                        editor: partyCombo,
                        renderer: editableColumnRenderer
                    },{
                        text: "Opening Balance",
                        dataIndex: 'balance',
                        flex: 1,
                        sortable: false
                    },{
                        text: "Debit detail",
                        dataIndex: 'debitDetail',
                        flex: 2,
                        sortable: false,
                        field: {
                            allowBlank: false
                        },
                        renderer: editableColumnRenderer
                    },{
                        text: "Debit amt",
                        dataIndex: 'debitAmt',
                        flex: 1,
                        //align: 'right',
                        sortable: false,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer
                    }, {
                        xtype: 'checkcolumn',
                        header: 'Cheque debit?',
                        dataIndex: 'isChequeDebit',
                        width:'20px',
                        listeners : {
                            checkchange: function (editor, e, eOpts) {
                                onPartyTransUpdate();
                            }
                        }
                    },{
                        text: "Credit detail",
                        dataIndex: 'creditDetail',
                        flex: 2,
                        sortable: false,
                        field: {
                            allowBlank: false
                        },
                        renderer: editableColumnRenderer
                    },{
                        text: "Credit amt",
                        dataIndex: 'creditAmt',
                        flex: 1,
                        sortable: false,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer
                    },getRowActionsColumns("PartyTransaction", onPartyTransUpdate )]
                },{
                    xtype: 'gridpanel',
                    id:'empTransGrid',
                    height: 'auto',
                    store: buildEmptyEmployeeTransactionStore(),
                    forceFit: true,
                    viewConfig: {
                        loadMask: false,
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
                            onEmpTransUpdate();
                        },
                        aftercellclick: function(iView, iCellEl, iColIdx, iRecord, iRowEl, iRowIdx, iEvent) {
                            var fieldName = iView.getGridColumns()[iColIdx].dataIndex;
                            if(fieldName === 'partyName') {
                                partyCombo.onTriggerClick();
                            }
                        }
                    },
                    columns:[{
                        text: "Employee name",
                        dataIndex: 'partyName',
                        flex: 2,
                        sortable: false,
                        editor: employeeCombo,
                        renderer: editableColumnRenderer
                    },{
                        text: "Salary detail",
                        dataIndex: 'salaryDetail',
                        flex: 2,
                        sortable: false,
                        field: {
                            allowBlank: false
                        },
                        renderer: editableColumnRenderer
                    },{
                        text: "Salary amt",
                        dataIndex: 'salaryAmt',
                        flex: 1,
                        //align: 'right',
                        sortable: false,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer
                    },{
                        text: "Incentive detail",
                        dataIndex: 'incentiveDetail',
                        flex: 2,
                        sortable: false,
                        field: {
                            allowBlank: false
                        },
                        renderer: editableColumnRenderer
                    },{
                        text: "Incentive amt",
                        dataIndex: 'incentiveAmt',
                        flex: 1,
                        sortable: false,
                        field: numberFieldConfig(),
                        renderer: editableColumnRenderer
                    },getRowActionsColumns("EmployeeTransaction", onEmpTransUpdate )]
                }]
        }]
    });
    return mainContent;
};

function onDailyStmtSave() {
    saveObj("SAVE");
}

function validateDailyStmt(callback) {
    var errors = [];
    var warnings = [];


    var meterSaleStore = Ext.getCmp('meterSaleGrid').store;
    var saleLiters = 0;
    meterSaleStore.each(function(record) {
        saleLiters = saleLiters + record.data.totalSale;
    });

    if(saleLiters<=0) {
        warnings.push("No meter sales added today.");
    }

    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;
    partyTransGridStore.each(function(record) {
        if(record.data.isChequeDebit && (record.data.debitAmt>record.data.balance)) {
            warnings.push("The cheque amt " + record.data.debitAmt +"Rs by " + record.data.partyName +" is higher than the amount owed " + record.data.balance + "Rs");
        }
    });

    checkErrorsWarningsAndProceed(errors, warnings, callback);
    return true;
}

function onDailyStmtSubmit() {
    var saveType = "SUBMIT";
    validateDailyStmt( function() {
        saveObj(saveType);
    });
}

function autoSave() {
    if(!Ext.getCmp("ds-notification")){
        clearTimeout(autoSaveTimeout);
        return;
    }
    if(lastEditDate && (!lastSavedDate || lastEditDate > lastSavedDate)) {
        onDailyStmtSave();
    }
    autoSaveTimeout = setTimeout(autoSave, 30000);
}

function saveObj(type) {
    if( type == "SUBMIT" ) {
        var myMask = new Ext.LoadMask(Ext.getBody(), {msg: "Saving daily statement..."});
        myMask.show();
    }
    //Prepare object
    var dailyStatementObject = prepareObj(type);

    //Save
    Ext.Ajax.request({
        url: 'api/saveDailyStatement',
        method: 'POST',
        jsonData:dailyStatementObject,
        success: function(response) {
            if( type == "SUBMIT" ) {
                Ext.MessageBox.alert('Success', "Successfully saved the daily statement." + response.responseText);
                loadDateAndUserInfo(myMask);
            } else {
                lastSavedDate = new Date();
                autoSaveFailureCount=0;
                updateSavedTime();
            }
        },
        failure : function(response) {
            if(myMask) {
                myMask.hide();
            }
            if( type == "SUBMIT" ) {
                Ext.Msg.alert("Error", "Failed to save the statement. Please try again. <br/> " + response.responseText);
            } else {
                autoSaveFailureCount++;
                if (autoSaveFailureCount>2) {
                    Ext.Msg.alert("Error", "Auto save failed for "+ autoSaveFailureCount + " times. " + getLastSavedText() + " <br/> Suspect issue with internet connection." + response.responseText);
                }
            }
        }
    });
}

function deleteSavedStmt(record) {
    //Delete
    Ext.Ajax.request({
        url: 'api/deleteSavedDailyStatement?date=' + bunkCache.infos.todayDate,
        method: 'DELETE',
        success: function() {
            updateSubPanel(record);
        }
    });
}

function updateSavedTime() {
    var notificationCmp = Ext.getCmp("ds-notification");
    if(!notificationCmp){
        clearInterval(autoUpdateSavedDate);
        return;
    }
    notificationCmp.setText(getLastSavedText());
}

function getLastSavedText() {
    if(lastSavedDate) {
        var diff = Math.round(Math.abs(lastSavedDate-new Date())/1000);
        return "Last saved at " + lastSavedDate.ddmmyyyyhhmmss() + " (" + diff + " secs ago)";
    }
    return "";
}

function prepareObj(saveType) {
    var ds = {
        date:bunkCache.infos.todayDate,
        type:saveType,
        closingBalance:Ext.getCmp('clBal-field').getValue()
    };

    var officeCashGridStore = Ext.getCmp('officeCashGrid').store;
    var officeCash = [];
    officeCashGridStore.each( function(record) {
        var offCash = {
            id:record.data.id,
            toOffice:record.data.toOffice,
            paidToBank:record.data.paidToBank
        }
        officeCash.push(offCash);
    });

    ds['officeCash'] = officeCash;

    var meterSaleStore = Ext.getCmp('meterSaleGrid').store;
    var meterSales = [];
    meterSaleStore.each(function(record) {
        var meterClosing = {
            meterId:record.data.meterId,
            totalSale:record.data.totalSale,
            closingReading:record.data.closingReading,
            testSale:record.data.testSale
        }
        meterSales.push(meterClosing);
    });
    ds['meterSales'] = meterSales;

    var tankSaleGridStore = Ext.getCmp('tankSaleGrid').store;
    var tankSales = [];
    tankSaleGridStore.each(function(record) {
        var obj = {
            tankId:record.data.tankId,
            productId:record.data.productId,
            dipStock:record.data.dipStock,
            testSale:record.data.testSale,
            diffToday:record.data.diffToday,
            diffThisMonth:record.data.newDiffThisMonth,
            sale:record.data.sale
        }
        tankSales.push(obj);
    });
    ds['tankSales'] = tankSales;

    var fuelSaleGridStore = Ext.getCmp('fuelSaleGrid').store;
    var fuelSales = [];
    fuelSaleGridStore.each(function(record) {
        var obj = {
            productId : record.data.productId,
            totalSale : record.data.totalSale,
            testSale : record.data.testSale,
            unitSellingPrice : record.data.unitSellingPrice,
            actualSale : record.data.actualSale
        }
        fuelSales.push(obj);
    });
    ds['fuelSales'] = fuelSales;

    var oilSaleGridStore = Ext.getCmp('oilSaleGrid').store;
    var lubesSales = [];
    oilSaleGridStore.each(function(record) {
        if(record.data.actualSale >0) {
            var obj = {
                productId : record.data.productId,
                totalSale : record.data.actualSale,
                discountPerUnit : record.data.discountPerUnit
            }
            lubesSales.push(obj);
        }
    });
    ds['lubesSales'] = lubesSales;

    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;
    var partyTrans = [];
    partyTransGridStore.each(function(record) {
        if(record.data.debitAmt >0 || record.data.creditAmt>0 && (!record.data.notEditable || record.data.notEditable == false)) {
            var obj = {
                partyId: record.data.partyId,
                debitDetail: record.data.debitDetail,
                debitAmt: record.data.debitAmt,
                creditDetail: record.data.creditDetail,
                creditAmt: record.data.creditAmt,
                isChequeDebit: record.data.isChequeDebit,
                slNo: record.data.slNo
            };
            partyTrans.push(obj);
        }
    });
    ds['partyTrans'] = partyTrans;

    var empTransGridStore = Ext.getCmp('empTransGrid').store;
    var empTrans = [];
    empTransGridStore.each(function(record) {
        if(record.data.salaryAmt >0 || record.data.incentiveAmt>0) {
            var obj = {
                partyId: record.data.partyId,
                salaryDetail: record.data.salaryDetail,
                salaryAmt: record.data.salaryAmt,
                incentiveDetail: record.data.incentiveDetail,
                incentiveAmt: record.data.incentiveAmt
            };
            empTrans.push(obj);
        }
    });
    ds['empTrans'] = empTrans;

    return ds;
}
function onOilSaleUpdate() {
    var oilSaleStore = Ext.getCmp('oilSaleGrid').store;
    oilSaleStore.each( function(record) {
        if(record.data.productName.length<=0) {
            record.set("actualSale", 0);
        }
        //Avoid negative sale/discount
        if(record.data.discountPerUnit < 0 || (record.data.discountPerUnit> record.data.unitSellingPrice)) {
            record.set('discountPerUnit', 0);
        }
        if(record.data.actualSale <= 0) {
            record.set('actualSale', 0);
            record.set('discountPerUnit', 0);
        }

        setOilTransTotalSaleAmt(record);
    });
    updateClosingBalance();

}

function onOfficeCashUpdate() {
    var officeCashGridStore = Ext.getCmp('officeCashGrid').store;
    officeCashGridStore.each( function(record) {
        //Avoid negative sale/discount
        if(record.data.toOffice < 0 ) {
            record.set('toOffice', 0);
        }
        if(record.data.paidToBank < 0) {
            record.set('paidToBank', 0);
        }

        if(record.data.name == 'OFFICE CHEQUE') {
            record.set('toOffice', getChequeAmtFromPartyTransactions()); //Get from party transactions
        }

        record.set('clBal', (record.data.opBal + record.data.toOffice - record.data.paidToBank).toFixed(2));
    });
    updateClosingBalance();
}

function onPartyTransUpdate() {
    onOfficeCashUpdate();
}

function onEmpTransUpdate() {
    var empTransGridStore = Ext.getCmp('empTransGrid').store;
    empTransGridStore.each( function(record) {
        if(record.data.partyName.length<=0) {
            record.set("salaryAmt", 0);
            record.set("incentiveAmt", 0);
            record.set("salaryDetail", '');
            record.set("incentiveDetail", '');
        } else {
            if(record.data.salaryAmt < 0) {
                record.set('salaryAmt', 0);
            }
            if(record.data.incentiveAmt < 0) {
                record.set('incentiveAmt', 0);
            }
        }
    });
    updateClosingBalance();
}
function getChequeAmtFromPartyTransactions() {
    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;
    var chequeAmt = 0;
    partyTransGridStore.each( function(record) {
        if(record.data.partyName.length<=0) {
            record.set("debitAmt", 0);
            record.set("creditAmt", 0);
            record.set("creditDetail", '');
            record.set("debitDetail", '');
            record.set("isChequeDebit", false);
        } else {
            if(record.data.debitAmt < 0) {
                record.set('debitAmt', 0);
            }
            if(record.data.creditAmt < 0) {
                record.set('creditAmt', 0);
            }
        }

        if(record.data.isChequeDebit === true) {
            chequeAmt =  chequeAmt + record.data.debitAmt;
        }
    });
    return chequeAmt.toFixed(2);
}


function onMeterSaleUpdate() {
    var meterSaleStore = Ext.getCmp('meterSaleGrid').store;
    //Group amt by product
    var groupedSaleAmtByTankId = {};
    var groupedTestAmtByTankId = {};
    var groupedSaleAmtByProdId = {};
    var groupedTestAmtByProdId = {};

    meterSaleStore.each(function(record) {
        //Update sale, test
        if(record.data.closingReading < record.data.finalReading) {
            //Reset closing
            record.set('closingReading', record.data.finalReading);
        }

        //Update sale
        record.set('totalSale', (record.data.closingReading - record.data.finalReading).toFixed(2));

        //Check test sale, reset if higher than sale
        if(record.data.totalSale < record.data.testSale) {
            //Reset testSale
            record.set('testSale', 0);
        }

        //Update actual sale
        record.set('actualSale', (record.data.totalSale - record.data.testSale).toFixed(2));


        if(groupedSaleAmtByTankId[record.data.tankId]) {
            groupedSaleAmtByTankId[record.data.tankId] = groupedSaleAmtByTankId[record.data.tankId] + record.data.actualSale;
        } else {
            groupedSaleAmtByTankId[record.data.tankId] = record.data.actualSale;
        }

        if(groupedTestAmtByTankId[record.data.tankId]) {
            groupedTestAmtByTankId[record.data.tankId] = groupedTestAmtByTankId[record.data.tankId] + record.data.testSale;
        } else {
            groupedTestAmtByTankId[record.data.tankId] = record.data.testSale;
        }

        if(groupedSaleAmtByProdId[record.data.productId]) {
            groupedSaleAmtByProdId[record.data.productId] = groupedSaleAmtByProdId[record.data.productId] + record.data.actualSale;
        } else {
            groupedSaleAmtByProdId[record.data.productId] = record.data.actualSale;
        }

        if(groupedTestAmtByProdId[record.data.productId]) {
            groupedTestAmtByProdId[record.data.productId] = groupedTestAmtByProdId[record.data.productId] + record.data.testSale;
        } else {
            groupedTestAmtByProdId[record.data.productId] = record.data.testSale;
        }
    });

    var tankSaleGridStore = Ext.getCmp('tankSaleGrid').store;

    tankSaleGridStore.each(function(record) {
        if(groupedSaleAmtByTankId[record.data.tankId]) {
            record.set("sale", groupedSaleAmtByTankId[record.data.tankId].toFixed(2));
        } else {
            record.set("sale", 0.0);
        }

        if(groupedTestAmtByTankId[record.data.tankId]) {
            record.set("testSale", groupedTestAmtByTankId[record.data.tankId].toFixed(2));
        } else {
            record.set("testSale", 0);
        }

        //Set stock after sale
        record.set("stockAfterSale", (record.data.closingStock - record.data.sale).toFixed(2));

        //Avoid negative dip stock
        if(record.data.dipStock < 0) {
            //Reset closing
            record.set('dipStock', 0);
        }

        if(record.data.dipStock > 0) {
            record.set("diffToday", (record.data.dipStock - record.data.stockAfterSale).toFixed(2));
            record.set("newDiffThisMonth", (record.data.diffThisMonth + record.data.diffToday).toFixed(2));
        }
    });

    var fuelSaleGridStore = Ext.getCmp('fuelSaleGrid').store;

    fuelSaleGridStore.each(function(record) {
        if(groupedSaleAmtByProdId[record.data.productId]) {
            record.set("actualSale", groupedSaleAmtByProdId[record.data.productId].toFixed(2));
            record.set("actualSaleAmt", (groupedSaleAmtByProdId[record.data.productId] * record.data.unitSellingPrice).toFixed(2));
        } else {
            record.set("actualSale", 0.0);
            record.set("actualSaleAmt", 0.0);
        }

        if(groupedTestAmtByProdId[record.data.productId]) {
            record.set("testSale", groupedTestAmtByProdId[record.data.productId].toFixed(2));
            record.set("testSaleAmt", (groupedTestAmtByProdId[record.data.productId] * record.data.unitSellingPrice).toFixed(2));
        } else {
            record.set("testSale", 0);
            record.set("testSaleAmt", 0);
        }
        record.set("totalSale", (record.data.testSale + record.data.actualSale).toFixed(2));
        record.set("totalSaleAmt", (record.data.totalSale * record.data.unitSellingPrice).toFixed(2));
    });

    updateClosingBalance();
}

function updateClosingBalance() {
    var clBalText = Ext.getCmp('openBal-field').getValue();
    if(clBalText == "" || isNaN(clBalText)) {
        return;
    }
    var clBal = parseFloat( clBalText);

    var fuelSaleGridStore = Ext.getCmp('fuelSaleGrid').store;
    fuelSaleGridStore.each(function(record) {
        clBal = clBal + record.data.actualSaleAmt;
    });

    var oilSaleGridStore = Ext.getCmp('oilSaleGrid').store;
    oilSaleGridStore.each(function(record) {
        clBal = clBal + record.data.totalSaleAmt;
    });

    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;
    partyTransGridStore.each(function(record) {
        if(!record.data.isChequeDebit) {
            clBal = clBal + record.data.debitAmt;
        }
        clBal = clBal - record.data.creditAmt;
    });

    var empTransGridStore = Ext.getCmp('empTransGrid').store;
    empTransGridStore.each(function(record) {
        clBal = clBal - record.data.salaryAmt;
        clBal = clBal - record.data.incentiveAmt;
    });

    var officeCashGridStore = Ext.getCmp('officeCashGrid').store;
    officeCashGridStore.each( function(record) {
        if(record.data.name == 'OFFICE CASH') {
            clBal = clBal - record.data.toOffice;
        }
    });

    lastEditDate = new Date();

    if(isNaN(clBal)) {
        Ext.getCmp('clBal-field').setValue(0.00);
    } else {
        Ext.getCmp('clBal-field').setValue(clBal.toFixed(2));
    }
}

function notifyAndRemoveStalePartyTransactions() {
    notifyAndRemoveStaleTransactions(Ext.getCmp('partyTransGrid'), Ext.getCmp('partyCombo').store, "partyId", "partyName", "party[s]");
}

function notifyAndRemoveStaleEmpTransactions() {
    notifyAndRemoveStaleTransactions(Ext.getCmp('empTransGrid'), Ext.getCmp('empCombo').store, "partyId", "partyName", "employee[s]");
}

function notifyAndRemoveStaleTransactions(grid, comboStore, idField, nameField, label) {
    if(grid) {
        var store = grid.store;
        var recordsToBeRemoved = [];
        var removedMsg = "";

        store.each(function(record) {
            if(record.get(idField) && record.get(idField)>0) {
                var item = comboStore.getById(record.get(idField));
                if(!item) {
                    //Party no longer there. Was potentially deactivated.Remove the record.
                    recordsToBeRemoved.push(record);

                    if(removedMsg.length > 0) {
                        //Check if the party is already mentioned
                        if(removedMsg.indexOf(record.get(nameField)) < 0) {
                            removedMsg = removedMsg + ", " + record.get(nameField);
                        }
                    } else {
                        removedMsg = removedMsg + record.get(nameField);
                    }
                }
            }
        });

        for(var i = 0 ; i < recordsToBeRemoved.length; i++) {
            store.remove(recordsToBeRemoved[i]);
        }
        if(removedMsg.length > 0) {
            Ext.create('Ext.window.MessageBox', {
                // set closeAction to 'destroy' if this instance is not
                // intended to be reused by the application
                closeAction: 'destroy'
            }).show({
                title: 'Alert',
                buttons: Ext.Msg.OK,
                msg: "Removing the transactions for " + removedMsg + " since these " + label + "are deactivated. Closing balance will be updated accordingly"
            });

            //Ext.Msg.alert("Alert", "Removing the transactions for " + removedMsg + " since these " + label + "are deactivated. Closing balance will be updated accordingly");
            updateClosingBalance();
        }
    }
}

function getSavedTransactions() {
    if (!loadingSavedTransactions && !loadedSavedTransactions) {
        loadingSavedTransactions = true;
        getDataWithAjax('api/dailyStatement?date=' + bunkCache.infos.todayDate, function(response){
            loadedSavedTransactions = true;
            loadingSavedTransactions = false;
            savedDailyStmt = Ext.decode(response.responseText);
            var opBalField = Ext.getCmp('openBal-field');
            if(opBalField) {
                opBalField.setValue(savedDailyStmt.settlement? savedDailyStmt.settlement.closingBal:0.0);
            }
            onMeterSaleUpdate();
            loadSavedTransactions();
            if(dailyStatementLoadMask) {
                dailyStatementLoadMask.hide();
            }
        },function(response){
            loadingSavedTransactions = false;
            savedDailyStmt = {};
            console.log('failed to read info from server:' + response);
            showFailedMask();
        });
    }
}


function loadSavedTransactions() {
    if(!partyComboStoreLoaded || !loadedSavedTransactions || !empComboStoreLoaded  || !oilComboStoreLoaded || dataLoaded) {
        //One of the data is not loaded. Defer this load
        return;
    }
    dataLoaded = true;
    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;
    var empTransGridStore = Ext.getCmp('empTransGrid').store;
    var partyComboStore = Ext.getCmp('partyCombo').store;
    var empComboStore = Ext.getCmp('empCombo').store;


    if(savedDailyStmt.closingStatement) {
        if(savedDailyStmt.closingStatement.partyTransactions) {
            for(var i = 0 ; i < savedDailyStmt.closingStatement.partyTransactions.length; i++) {
                var savedTrans = savedDailyStmt.closingStatement.partyTransactions[i];
                if(!savedTrans.partyId || savedTrans.transactionType.indexOf("_S") > 0) {
                    continue;
                }

                //Verify that the partyID is present in the dropdown
                var partyTrans = new PartyTransaction();
                var party = partyComboStore.getById(savedTrans.partyId);
                partyTrans.set('partyId',savedTrans.partyId);
                if(party) {
                    partyTrans.set('partyName',party.data.name);
                } else {
                    continue;
                }

                partyTrans.set('slNo',savedTrans.slNo);
                partyTrans.set('transactionType',savedTrans.transactionType);
                if(savedTrans.transactionType.indexOf("CREDIT") >= 0) {
                    partyTrans.set('creditDetail',savedTrans.transactionDetail);
                    partyTrans.set('creditAmt',savedTrans.amount);
                } else {
                    partyTrans.set('debitDetail',savedTrans.transactionDetail);
                    partyTrans.set('debitAmt',savedTrans.amount);
                }
                partyTrans.set('notEditable',true);
                partyTransGridStore.insert(0, partyTrans);
            }
        }
    };

    if(savedDailyStmt.savedDailyStatement) {
        var savedDailyStatement = savedDailyStmt.savedDailyStatement;
        //Load office cash values
        if(savedDailyStatement.officeCash) {
            var officeCashGridStore = Ext.getCmp('officeCashGrid').store;
            for(var i = 0 ; i < savedDailyStatement.officeCash.length; i++) {
                var savedTrans = savedDailyStatement.officeCash[i];
                if(!savedTrans.id) {
                    continue;
                }

                officeCashGridStore.each(function(record) {
                    if(record.data.id == savedTrans.id) {
                        record.set('paidToBank',savedTrans.paidToBank);
                        record.set('toOffice',savedTrans.toOffice);
                    }
                });
            }
        }

        //Load meter readings
        if(savedDailyStatement.meterSales) {
            var meterSaleStore = Ext.getCmp('meterSaleGrid').store;
            for(var i = 0 ; i < savedDailyStatement.meterSales.length; i++) {
                var savedTrans = savedDailyStatement.meterSales[i];
                if(!savedTrans.meterId) {
                    continue;
                }

                meterSaleStore.each(function(record) {
                    if(record.data.meterId == savedTrans.meterId && savedTrans.closingReading > record.data.finalReading) {
                        record.set('closingReading',savedTrans.closingReading);
                        record.set('testSale',savedTrans.testSale);
                        record.set('totalSale',savedTrans.totalSale);
                    }
                });
            }
        }

        //Tank values
        if(savedDailyStatement.tankSales) {
            var tankSaleGridStore = Ext.getCmp('tankSaleGrid').store;
            for(var i = 0 ; i < savedDailyStatement.tankSales.length; i++) {
                var savedTrans = savedDailyStatement.tankSales[i];
                if(!savedTrans.tankId) {
                    continue;
                }

                tankSaleGridStore.each(function(record) {
                    if(record.data.tankId == savedTrans.tankId) {
                        record.set('dipStock',savedTrans.dipStock);
                    }
                });
            }
        }

        //Fuel sales.


        var insertIndex = 0;
        //Lube sales
        if(savedDailyStatement.lubesSales) {
            var oilSaleGridStore = Ext.getCmp('oilSaleGrid').store;
            var lubeComboStore = Ext.getCmp('lubeCombo').store;
            for(var i = 0 ; i < savedDailyStatement.lubesSales.length; i++) {
                var savedTrans = savedDailyStatement.lubesSales[i];
                if(!savedTrans.productId) {
                    continue;
                }

                //Verify that the partyID is present in the dropdown
                var oilTrans = new LubeSale();
                var prod = lubeComboStore.getById(savedTrans.productId);
                oilTrans.set('productId',savedTrans.productId);
                if(!prod) {
                    continue;
                }

                oilTrans.set("productName", prod.data.productName + ' - ' + prod.data.unitSellingPrice +'Rs');
                oilTrans.set("currentStock", prod.data.currentStock);
                oilTrans.set("productId", prod.data.productId);
                oilTrans.set("unitSellingPrice", prod.data.unitSellingPrice);
                oilTrans.set("actualSale", savedTrans.totalSale);
                oilTrans.set("discountPerUnit", savedTrans.discountPerUnit);

                setOilTransTotalSaleAmt(oilTrans);

                oilSaleGridStore.insert(insertIndex++, oilTrans);
            }
        }

        insertIndex = 0;
        //Load party transactions
        if(savedDailyStatement.partyTrans) {
            for(var i = 0 ; i < savedDailyStatement.partyTrans.length; i++) {
                var savedTrans = savedDailyStatement.partyTrans[i];
                if(!savedTrans.partyId) {
                    continue;
                }

                //Verify that the partyID is present in the dropdown
                var partyTrans = new PartyTransaction();
                var party = partyComboStore.getById(savedTrans.partyId);
                partyTrans.set('partyId',savedTrans.partyId);
                if(party) {
                    partyTrans.set('partyName',party.data.name);
                } else {
                    continue;
                }

                partyTrans.set('slNo',savedTrans.slNo);
                if(savedTrans.creditAmt > 0) {
                    partyTrans.set('creditDetail',savedTrans.creditDetail);
                    partyTrans.set('creditAmt',savedTrans.creditAmt);
                }
                if(savedTrans.debitAmt > 0){
                    partyTrans.set('debitDetail',savedTrans.debitDetail);
                    partyTrans.set('debitAmt',savedTrans.debitAmt);
                    partyTrans.set('isChequeDebit',savedTrans.isChequeDebit);
                }
                partyTransGridStore.insert(insertIndex++, partyTrans);
            }
        }

        insertIndex = 0;
        //Load employee transactions
        if(savedDailyStatement.empTrans) {
            for(var i = 0 ; i < savedDailyStatement.empTrans.length; i++) {
                var savedTrans = savedDailyStatement.empTrans[i];
                if(!savedTrans.partyId) {
                    continue;
                }

                //Verify that the partyID is present in the dropdown
                var empTrans = new EmployeeTransaction();
                var employee = empComboStore.getById(savedTrans.partyId);
                empTrans.set('partyId',savedTrans.partyId);
                if(employee) {
                    empTrans.set('partyName',employee.data.name);
                } else {
                    continue;
                }
                empTrans.set('salaryDetail',savedTrans.salaryDetail);
                empTrans.set('salaryAmt',savedTrans.salaryAmt);
                empTrans.set('incentiveDetail',savedTrans.incentiveDetail);
                empTrans.set('incentiveAmt',savedTrans.incentiveAmt);
                empTransGridStore.insert(insertIndex++, empTrans);
            }

        }
        onOfficeCashUpdate();
        onMeterSaleUpdate();
        onEmpTransUpdate();
    }
}

function setOilTransTotalSaleAmt(oilTrans) {
    if(oilTrans.data.actualSale > 0) {
        oilTrans.set( 'totalSaleAmt', ((oilTrans.data.unitSellingPrice - oilTrans.data.discountPerUnit) * oilTrans.data.actualSale).toFixed(2));
    } else {
        oilTrans.set( 'totalSaleAmt', 0);
    }
}



