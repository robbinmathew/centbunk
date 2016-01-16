//Defines all stores

function buildTankReceiptStore(date) {
    return Ext.create('Ext.data.Store', {
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
}

function buildAvailableProductList(type) {
    return Ext.create('Ext.data.Store', {
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
}