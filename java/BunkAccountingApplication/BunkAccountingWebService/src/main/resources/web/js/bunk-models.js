//Defines all models.
Ext.define('TankReceipt', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'productId', type: 'int'},
        'productType',
        {name: 'tankId', type: 'int'},
        'tankName',
        {name: 'closingStock', type: 'float'},
        {name: 'newDiffThisMonth', type: 'float'},
        {name: 'diffThisMonth', type: 'float'},
        {name: 'diffToday', type: 'float'},
        {name: 'receiptAmt', type: 'float'},
        {name: 'sale', type: 'float'},
        {name: 'testSale', type: 'float'},
        {name: 'dipStock', type: 'float'},
        {name: 'stockAfterSale', type: 'float'},
        {name: 'afterReceiptStock', type: 'float'}
    ],
    idProperty: 'tankId'
});

Ext.define('FuelReceipt', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'productId', type: 'int'},
        'productName',
        {name: 'unitSellingPrice', type: 'float'},
        {name: 'marginPerUnit', type: 'float'},
        {name: 'currentStock',mapping:'closingStock', type: 'float'},

        {name: 'receiptAmt', type: 'float'},
        {name: 'costOnInv', type: 'float'},

        {name: 'totalSale', type: 'float'},
        {name: 'totalSaleAmt', type: 'float'},
        {name: 'actualSale', type: 'float'},
        {name: 'actualSaleAmt', type: 'float'},
        {name: 'testSale', type: 'float'},
        {name: 'testSaleAmt', type: 'float'},
        {name: 'discountPerUnit', type: 'float'},

        //Rate change fields
        {name: 'newUnitSellingPrice', type: 'float'},
        {name: 'rateDiff', type: 'float'},
        {name: 'margin', type: 'float'},
        {name: 'cashDiff', type: 'float'}
    ],
    idProperty: 'productId'
});


Ext.define('ProdReceipt', {
    extend: 'Ext.data.Model',
    fields: [
        'id',
        {name: 'productId', type: 'int'},
        'productName',
        {name: 'unitSellingPrice', type: 'float'},
        {name: 'marginPerUnit', type: 'float'},
        {name: 'currentStock',mapping:'closingStock', type: 'float'},

        {name: 'receiptAmt', type: 'float'},
        'comment',
        {name: 'costOnInv', type: 'float'}
    ],
    idProperty: 'id'
});


Ext.define('ProductStatus', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'productId', type: 'int'},
        'productName',
        {name: 'unitSellingPrice', type: 'float'},
        {name: 'margin', type: 'float'},
        {name: 'currentStock',mapping:'closingStock', type: 'float'}
    ],
    idProperty: 'productId'
});



Ext.define('LubeSale', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id'},
        {name: 'productId', type: 'int'},
        'productName',
        {name: 'unitSellingPrice', type: 'float'},
        {name: 'marginPerUnit', type: 'float'},
        {name: 'currentStock',mapping:'closingStock', type: 'float'},

        {name: 'totalSale', type: 'float'},
        {name: 'totalSaleAmt', type: 'float'},
        {name: 'discountPerUnit', type: 'float'}
    ],
    idProperty: 'id'
});



Ext.define('MeterClosing', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'productId', type: 'int'},
        {name: 'meterId', type: 'int'},
        {name: 'tankId', type: 'int'},
        'meterName',
        {name: 'finalReading', type: 'float'},
        {name: 'totalSale', type: 'float'},
        {name: 'testSale', type: 'float'},
        {name: 'actualSale', type: 'float'},
        {name: 'closingReading', type: 'float'}
    ],
    idProperty: 'meterId'
});

Ext.define('PartyTransaction', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id'},
        {name: 'slNo', type: 'int'},
        {name: 'partyId', type: 'int'},
        'partyName',
        'debitDetail',
        'creditDetail',
        'transactionType',
        {name: 'debitAmt', type: 'float'},
        {name: 'balance', type: 'float'},
        {name: 'isChequeDebit', type: 'bool'},
        {name: 'notEditable', type: 'bool'},
        {name: 'creditAmt', type: 'float'}
    ],
    idProperty: 'id'
});

Ext.define('EmployeeTransaction', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id'},
        {name: 'partyId', type: 'int'},
        'partyName',
        'salaryDetail',
        'incentiveDetail',
        {name: 'salaryAmt', type: 'float'},
        {name: 'incentiveAmt', type: 'float'}
    ],
    idProperty: 'id'
});

Ext.define('Party', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int'},
        'name',
        {name: 'balance', type: 'float'}
    ],
    idProperty: 'id'
});


Ext.define('PartyResult', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'partyId', type: 'int'},
        {name: 'date', type: 'int'},
        {name: 'slNo', type: 'int'},
        'partyName',
        'partyDetail',
        'partyPhone',
        'partyStatus',
        {name: 'balance', type: 'float'}
    ],
    idProperty: 'partyId'
});

Ext.define('OfficeCash', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int'},
        'name',
        {name: 'opBal', mapping:'balance', type: 'float'},
        {name: 'toOffice', type: 'float'},
        {name: 'paidToBank', type: 'float'},
        {name: 'clBal', type: 'float'}
    ],
    idProperty: 'id'
});


//Defines all stores

function buildTankReceiptStore(date, loadMask) {
    var store = Ext.create('Ext.data.Store', {
        //pageSize: 20,
        model: 'TankReceipt',
        autoLoad: true,
        proxy: {
            type: 'rest',
            url: 'api/tankClosingStock?date=' + date,
            reader: {
                type: 'json'
            }
        }
    });
    if(loadMask) {
        loadMask.bindStore(store);
    }
    return store;
}

function buildOfficeCashStore(loadMask) {
    var store = Ext.create('Ext.data.Store', {
        //pageSize: 20,
        model: 'OfficeCash',
        autoLoad: true,
        proxy: {
            type: 'rest',
            url: 'api/officeClosingBalance',
            reader: {
                type: 'json'
            }
        }
    });
    if(loadMask) {
        loadMask.bindStore(store);
    }
    return store;
}

//deprecated
function buildAvailableProductListStore(type, loadMask) {
    var store = Ext.create('Ext.data.Store', {
        //pageSize: 20,
        model: 'FuelReceipt',
        autoLoad: true,
        proxy: {
            type: 'rest',
            url: 'api/getAvailableProductList?type=' + type,
            reader: {
                type: 'json'
            }
        }
    });
    if(loadMask) {
        loadMask.bindStore(store);
    }
    return store;
}

function buildAvailableProductsStore(type, modelType, loadMask) {
    var store = Ext.create('Ext.data.Store', {
        model: modelType,
        autoLoad: true,
        proxy: {
            type: 'rest',
            url: 'api/getAvailableProductList?type=' + type,
            reader: {
                type: 'json'
            }
        }
    });
    if(loadMask) {
        loadMask.bindStore(store);
    }
    return store;
}

function buildProdReceiptStore() {
    var prodReceiptStore = new Ext.data.SimpleStore({
        model: 'ProdReceipt'
    });

    for (var i=0; i<5;i++) {
        var newLube = new ProdReceipt();
        newLube.set('id', 'new-lube-' + i);
        prodReceiptStore.add(newLube);
    }
    return prodReceiptStore;
}

function buildPartyListStore(type, loadMask) {
    var store = Ext.create('Ext.data.Store', {
        //pageSize: 20,
        model: 'Party',
        autoLoad: true,
        proxy: {
            type: 'rest',
            url: 'api/partiesClosingBalance?type=' + type,
            reader: {
                type: 'json'
            }
        }
    });
    if(loadMask) {
        loadMask.bindStore(store);
    }
    return store;
}

function buildAllPartyResultStores(type, loadMask, callback) {
    getDataWithAjax('api/parties?type=' + type,
        function (response) {
            var responseJson = Ext.decode(response.responseText);
            if (responseJson) {

                var parties = new Ext.data.SimpleStore({
                    model: 'PartyResult'
                });
                var employees = new Ext.data.SimpleStore({
                    model: 'PartyResult'
                });
                var banks = new Ext.data.SimpleStore({
                    model: 'PartyResult'
                });
                var storesArr = [parties, employees, banks];
                var arrayLength = responseJson.length;
                for (var i = 0; i < arrayLength; i++) {
                    var item = responseJson[i];

                    if(item.partyStatus == "INACTIVE_S") {
                        continue; // These are hidden system parties.
                    }

                    var party = new PartyResult();
                    party.set('partyId', item.partyId);
                    party.set('date', item.date);
                    party.set('slNo', item.slNo);
                    party.set('partyName', item.partyName);
                    party.set('partyDetail', item.partyDetail);
                    party.set('partyPhone', item.partyPhone);
                    party.set('partyStatus', item.partyStatus);
                    party.set('balance', item.balance);
                    party.commit();



                    if (item.partyId >= 1 && item.partyId < 250) {
                        employees.add(party);
                    } else if (item.partyId >= 250 && item.partyId < 300) {
                        banks.add(party);
                    } else {
                        parties.add(party);
                    }
                }
                window[callback](storesArr, loadMask);
            }
        },
        function (response) {
            console.log('failed to read info from server for buildAllPartyResultStores ' + type + ', response' + response);
        });
}

function buildEmployeeListStore(loadMask) {
    var store = Ext.create('Ext.data.Store', {
        //pageSize: 20,
        model: 'Party',
        autoLoad: true,
        proxy: {
            type: 'rest',
            url: 'api/employeesStatus',
            reader: {
                type: 'json'
            }
        }
    });
    if(loadMask) {
        loadMask.bindStore(store);
    }
    return store;
}

function buildEmptyProductSaleStore() {
    var prodSaleStore = new Ext.data.SimpleStore({
        model: 'LubeSale'
    });

    for (var i=0; i<5;i++) {
        prodSaleStore.add(new LubeSale());
    }
    return prodSaleStore;
}

function buildEmptyPartyTransactionStore() {
    var partyTransactionStore = new Ext.data.SimpleStore({
        model: 'PartyTransaction'
    });

    for (var i=0; i<10;i++) {
        partyTransactionStore.insert(0,new PartyTransaction());
    }
    return partyTransactionStore;
}

function buildEmptyEmployeeTransactionStore() {
    var partyTransactionStore = new Ext.data.SimpleStore({
        model: 'EmployeeTransaction'
    });

    for (var i=0; i<5;i++) {
        partyTransactionStore.insert(0,new EmployeeTransaction());
    }
    return partyTransactionStore;
}

function buildActiveMeterListStore(loadMask) {
    var store = Ext.create('Ext.data.Store', {
        //pageSize: 20,
        model: 'MeterClosing',
        autoLoad: true,
        proxy: {
            type: 'rest',
            url: 'api/activeMeterClosingReading',
            reader: {
                type: 'json'
            }
        }
    });
    if(loadMask) {
        loadMask.bindStore(store);
    }
    return store;
}

function buildJsonStoreWithData(data, fields, groupByFields, loadMask) {
    var allFields = groupByFields.concat(fields);
    var store = new Ext.data.JsonStore({
        autoLoad: false,
        sorters: [{
            property: 'DATE',
            direction: 'ASC'
        }],
        sortOnLoad: true,
        remoteSort: false,
        fields: allFields
    });

    store.loadData(data);
    if(loadMask) {
        loadMask.bindStore(store);
    }
    return store;
}

function addReportPanel(path, params, title, targetPanel, unit, colSize, renderType, heightPx ) {
    this.path = path;
    this.params = params;
    this.title = title;
    this.renderType = renderType;

    var summaryPanel = Ext.create('Ext.form.Panel', {
        title: title,
        frame: true,
        autoScroll: true,
        bodyStyle: 'padding:0px 0px 0',
        columnWidth: colSize,
        height:heightPx,
        layout: 'column',
        defaults: {
            anchor: '100%'
        },
        items: []

    });
    targetPanel.add(summaryPanel);

    this.unit = unit;
    var self = this;
    getDataWithAjax(path + "?" + paramsToQuery(params),
        function (response) {

            var responseJson = Ext.decode(response.responseText);
            var resultsStore = buildJsonStoreWithData(responseJson.results, responseJson.fields, responseJson.groupByFields);


            if (renderType == "chart") {
                this.tipsConfig = {
                    trackMouse: true,
                    style: 'background: #FFF;line-height: 50%',
                    height: 20 * responseJson.fields.length,
                    width: 250,
                    renderer: function (storeItem, item) {
                        var seriesTitle = item.series.title;
                        var text = "";
                        for(var i=0; i< responseJson.fields.length; i++) {
                            var fieldName = responseJson.fields[i];

                            text = text + "<p>";
                            if(fieldName == seriesTitle) {
                                text = text + "<mark>";
                            }
                            text = text + fieldName + ': ' + (storeItem.get(fieldName) ? storeItem.get(fieldName) : 0) + unit;
                            if(fieldName == seriesTitle) {
                                text = text + "</mark>";
                            }
                            text = text + "</p>";
                        }
                        this.setTitle(text);
                    }
                };

                var i=0;

                var seriesArray = [];
                for(var i=0; i< responseJson.fields.length; i++) {
                    seriesArray.push({
                        type: 'line',
                        axis: 'left',
                        title: responseJson.fields[i],
                        xField: 'DATE_TEXT',
                        yField: responseJson.fields[i],
                        style: {
                            'stroke-width': 4,
                            stroke: graphColorArray[i]
                        },
                        markerConfig: {
                            radius: 4,
                            type: 'circle',
                            fill: graphColorArray[i]
                        },
                        /*label:
                         {
                         display: 'over',
                         field: 'P_SALE',
                         renderer: function(val) {
                         return val + 'L';
                         }
                         },*/
                        highlight: {
                            fill: '#000',
                            radius: 5,
                            'stroke-width': 2,
                            stroke: '#fff'
                        },
                        tips: this.tipsConfig
                    });
                }


                var chartPanel = Ext.create('Ext.chart.Chart', {
                    xtype: 'chart',
                    //forceFit: true,
                    //padding: '10 0 0 0',
                    animate: true,
                    columnWidth: 1,
                    shadow: false,
                    height: (heightPx -50),
                    style: 'background: #fff;',
                    store: resultsStore,
                    legend: {
                        position: 'right',
                        boxStrokeWidth: 1,
                        labelFont: '6px Arial'
                    },
                    axes: [{
                        type: 'numeric',
                        fields: responseJson.fields,
                        position: 'left',
                        grid: true,
                        minimum: 0,
                        label: {
                            renderer: function (v) {
                                return v + unit;
                            }
                        }
                    }, {
                        type: 'category',
                        fields: "DATE_TEXT",
                        position: 'bottom',
                        grid: true,
                        label: {
                            rotate: {
                                degrees: -90
                            }
                        }
                    }],
                    series: seriesArray
                });
                summaryPanel.add(chartPanel);
            } else {
                //Table

                var i=0;
                var allFields = [];
                if (responseJson.groupByFields) {
                    allFields = allFields.concat(responseJson.groupByFields);
                }
                if (responseJson.fields) {
                    allFields = allFields.concat(responseJson.fields);
                }

                var colArray = [];
                for(var i=0; i< allFields.length; i++) {
                    var field = allFields[i];
                    if(field == "DATE" || field.endsWith("_ID")) {
                        continue;
                    }
                    colArray.push({
                        text: field,
                        dataIndex: field,
                        sortable: false,
                        flex: 1
                    });
                }


                var gridPanel = Ext.create('Ext.grid.Panel', {
                    xtype: 'gridpanel',
                    //forceFit: true,
                    //padding: '10 0 0 0',
                    animate: true,
                    columnWidth: 1,
                    shadow: false,
                    height:300,
                    style: 'background: #fff;',
                    store: resultsStore,
                    columns:colArray
                });
                summaryPanel.add(gridPanel);
            }


            summaryPanel.doLayout();
        },
        function (response) {
            console.log('failed to read info from server for report ' + title + ', response' + response);
        });
}




