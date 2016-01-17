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
        {name: 'receiptAmt', type: 'float'},
        {name: 'totalSale', type: 'float'},
        {name: 'totalSaleAmt', type: 'float'},
        {name: 'actualSale', type: 'float'},
        {name: 'actualSaleAmt', type: 'float'},
        {name: 'testSale', type: 'float'},
        {name: 'testSaleAmt', type: 'float'},
        {name: 'marginPerUnit', type: 'float'},
        {name: 'costOnInv', type: 'float'}
    ],
    idProperty: 'productId'
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

