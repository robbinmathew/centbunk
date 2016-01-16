//Defines all models.
Ext.define('TankReceipt', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'productId', type: 'int'},
        'productType',
        {name: 'tankId', type: 'int'},
        'tankName',
        {name: 'closingStock', type: 'float'},
        {name: 'receiptAmt', type: 'float'},
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
        {name: 'marginPerUnit', type: 'float'},
        {name: 'costOnInv', type: 'float'}
    ],
    idProperty: 'productId'
});

