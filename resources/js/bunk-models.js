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

Ext.define('LubeSale', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int'},
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
        {name: 'id', type: 'int'},
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
        {name: 'id', type: 'int'},
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

function buildFuelsSaleSummaryStore(start, end, loadMask) {
    var store = Ext.create('Ext.data.JsonStore', {
        //pageSize: 20,
        fields: ['DATE_TEXT', 'P_SALE', 'P_TEST', 'P_CL_STOCK', 'D_SALE', 'D_TEST', 'D_CL_STOCK'],
        autoLoad: true,
        proxy: {
            type: 'rest',
            url: 'api/fuelsSalesSummary?start=' + start + "&end=" + end,
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
