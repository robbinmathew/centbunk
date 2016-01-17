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

function buildAvailableProductListStore(type) {
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

function buildEmptyProductSaleStore() {
    var prodSaleStore = new Ext.data.SimpleStore({
        model: 'FuelReceipt'
    });

    prodSaleStore.insert(0, new FuelReceipt());
    prodSaleStore.insert(0, new FuelReceipt());
    prodSaleStore.insert(0, new FuelReceipt());
    prodSaleStore.insert(0, new FuelReceipt());

    return prodSaleStore;
}

function buildActiveMeterListStore() {
    return Ext.create('Ext.data.Store', {
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
}