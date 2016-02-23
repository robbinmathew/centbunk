var dailyStatement = {};
var partyComboStoreLoaded = false;
var loadedSavedTransactions = false;
var dataLoaded = false;
var dailyStatementLoadMask;
var dailyStatementLoadMask1;

function buildDailyStatementPanel(title, record) {
    //Reset all values
    dailyStatement = {};
    partyComboStoreLoaded = false;
    loadedSavedTransactions = false;
    dataLoaded = false;

    dailyStatementLoadMask1 = new Ext.LoadMask( Ext.getBody(), {
            msg:"Preparing for daily statement...",
            listeners: {
                beforehide: function (loadMask, eOpts) {
                    console.log("Test");
                    return loadMask.hideMask(loadMask, eOpts);
                }
            }
        });

    dailyStatementLoadMask1.show();

    var oilProdStore = buildAvailableProductListStore('SECONDARY_PRODUCTS', dailyStatementLoadMask);
    var oilProdCombo = new Ext.form.ComboBox({
        store: oilProdStore,
        valueField: "productId",
        editable: false,
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
            '{productName} - {unitSellingPrice}Rs',
            '</tpl>'
        ),
        listeners: {
            select: {
                fn: function(c, r, eopts) {
                    c.ownerCt.completeEdit();
                    var gridRecord = c.ownerCt.floatParent.getSelectionModel().getSelection()[0];
                    var comboRecord = r[0];
                    //Copy values to grid record
                    gridRecord.set("productName", comboRecord.data.productName + ' - ' + comboRecord.data.unitSellingPrice +'Rs');
                    gridRecord.set("currentStock", comboRecord.data.currentStock);
                    gridRecord.set("unitSellingPrice", comboRecord.data.unitSellingPrice);
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
                    gridRecord.set("id", comboRecord.data.productId + ":" + rowNo+1);
                }
            }
        }
    });
    var partyStore = buildPartyListStore('ALL_ACTIVE_NON_EMPLOYEE_PARTIES', dailyStatementLoadMask);
    partyStore.on('load',function (){
        partyComboStoreLoaded = true;
        getDataWithAjax('api/dailyStatement?date=' + bunkCache.infos.todayDate, function(response){
            dailyStatement = Ext.decode(response.responseText);
            Ext.getCmp('openBal-field').setValue(dailyStatement.settlement? dailyStatement.settlement.closingBal:0.0);
            loadedSavedTransactions = true;
            onMeterSaleUpdate();
            loadSavedTransactions();
            dailyStatementLoadMask1.hide();
        },function(response){
            dailyStatement = {};
            console.log('failed to read info from server:' + response);
            showFailedMask();
        });
    });

    var partyCombo = new Ext.form.ComboBox({
        id:'partyCombo',
        store: partyStore,
        valueField: "id",
        displayField: "name",
        editable: false,
        listeners: {
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
                    gridRecord.set("id", comboRecord.data.id + ":" + rowNo+1);
                }
            }
        }
    });
    var empStore = buildEmployeeListStore(dailyStatementLoadMask);

    var employeeCombo = new Ext.form.ComboBox({
        store: empStore,
        valueField: "id",
        displayField: "name",
        editable: false,
        listeners: {
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
                    gridRecord.set("id", comboRecord.data.id + ":" + rowNo+1);
                }
            }
        }
    });

    var mainContent = Ext.create('Ext.form.Panel', {
        id:'dailyStmtPanel',
        title: title,
        //frame:true,
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
            layout: 'hbox',
            //title:'Opening/Closing',
            //width:'100%',
            //height:'50px',
            defaultType: 'textfield',
            fieldDefaults: {
                msgTarget: 'side',
                labelSeparator : ' :',
                margins: '10 10 10 0',
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
                    disabled: true,
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
                        updateSubPanel(record);

                    }
                }
            ]
        },{
            xtype: 'gridpanel',
            id:'officeCashGrid',
            autoscroll: true,
            height: 'auto',
            store: buildOfficeCashStore(dailyStatementLoadMask),
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
                    onOfficeCashUpdate();
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
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Paid to bank",
                dataIndex: 'paidToBank',
                flex: 1,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
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
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
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
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
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
            store: buildTankReceiptStore(bunkCache.infos.todayDate, dailyStatementLoadMask),
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
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
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
            store: buildAvailableProductListStore('FUEL_PRODUCTS', dailyStatementLoadMask),
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
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Discount per unit",
                dataIndex: 'discountPerUnit',
                flex: 1,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Amount after discount",
                dataIndex: 'actualSaleAmt',
                flex: 1,
                sortable: false
            }]
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
                sortable: false,
                field: {
                    allowBlank: false
                }
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
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            }, {
                xtype: 'checkcolumn',
                header: 'Cheque debit?',
                dataIndex: 'isChequeDebit',
                width:'20px'
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
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            }]
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
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
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
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            }]
        }]
    });
    return mainContent;
};

function onDailyStmtSave() {
    saveObj("TEMP_SAVE");
}

function validateDailyStmt(callback) {
    var errors = [];
    var warnings = [];

    //warnings.push('No product receipt mentioned');

    if(errors.length > 0) {
        Ext.MessageBox.alert('Error', prepareErrorMsg("Please fix the below validation errors.", errors));
    }
    if(errors.length ==0 && warnings.length == 0) {
        callback();
    }

    if(warnings.length > 0) {
        var messageBox = Ext.create('Ext.window.MessageBox', {
            buttonText: {
                ok: 'Continue with submit.',
                yes: 'Yes',
                no: 'No',
                cancel: 'Cancel, let me correct them'
            }
        });
        messageBox.show({
            title: "Please review the below warnings",
            msg: prepareErrorMsg("", warnings),
            buttons: Ext.Msg.OKCANCEL,
            icon: Ext.MessageBox.WARNING,
            fn: function(btn){
                if (btn == "ok"){
                    callback();
                }
            }
        });
    }
    return true;
}

function onDailyStmtSubmit() {
    var saveType = "SUBMIT";
    validateDailyStmt( function() {
        saveObj(saveType);
    });
}

function saveObj(type) {
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Saving daily statement..."});
    myMask.show();
    //Prepare object
    var dailyStatement = prepareObj(type);

    //Save
    Ext.Ajax.request({
        url: 'api/saveDailyStatement',
        method: 'POST',
        jsonData:dailyStatement,
        success: function() {
            myMask.hide();
            Ext.MessageBox.alert('Success', "Successfully saved the daily statement.");
            updateSummaryPanel();
        },
        failure : function(response) {
            myMask.hide();
            Ext.Msg.alert("Error", "Failed to save the stock receipt. Please try again. <br/> " + response.responseText);
        }
    });
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
            finalReading:record.data.finalReading,
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
        if(record.data.totalSale >0) {
            var obj = {
                productId : record.data.productId,
                totalSale : record.data.totalSale,
                discountPerUnit : record.data.discountPerUnit
            }
            lubesSales.push(obj);
        }
    });
    ds['lubesSales'] = lubesSales;

    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;
    var partyTrans = [];
    partyTransGridStore.each(function(record) {
        if(record.data.debitAmt >0 || record.data.creditAmt>0 || !record.data.notEditable || record.data.notEditable == false) {
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

        if(record.data.actualSale > 0) {
            record.set( 'actualSaleAmt', ((record.data.unitSellingPrice - record.data.discountPerUnit)* record.data.actualSale).toFixed(2));
        } else {
            record.set( 'actualSaleAmt', 0);
        }
    });

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
    var clBalText = Ext.getCmp('openBal-field').getValue();
    if(clBalText == "" || isNaN(clBalText)) {
        return;
    }
    var clBal = parseFloat( clBalText);

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

    if(isNaN(clBal)) {
        Ext.getCmp('clBal-field').setValue(0.00);
    } else {
        Ext.getCmp('clBal-field').setValue(clBal.toFixed(2));
    }


}

function loadSavedTransactions() {
    if(!partyComboStoreLoaded || !loadedSavedTransactions || dataLoaded) {
        //One of the data is not loaded. Defer this load
        return;
    }
    dataLoaded = true;
    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;
    var partyComboStore = Ext.getCmp('partyCombo').store;

    if(dailyStatement.closingStatement) {
        if(dailyStatement.closingStatement.partyTransactions) {
            for(var i = 0 ; i < dailyStatement.closingStatement.partyTransactions.length; i++) {
                var savedTrans = dailyStatement.closingStatement.partyTransactions[i];
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
}



