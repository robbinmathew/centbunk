
function AbstractEntityTransactionBuilder (date, decimalCount, creditTransTypes) {
    this.decimalCount = decimalCount,
        this.date = date,
        this.entityBalances = {},                   //private final Map<Integer, BigDecimal> entityBalances;
        this.transactions = [],                     //protected final List<T> transactions;
        this.creditTransTypes = creditTransTypes   //private final List<String> creditTransTypes;
    //this.entityList = entityList                //final List<E> entitiesList,
    //protected final Map<Integer, E> entities;
    //private final Map<Integer, BigDecimal> entityBalances;
}

AbstractEntityTransactionBuilder.getEntityId = function() { //abstract Integer getEntityId(final E entity);
    alert('AbstractEntityTransactionBuilder.getEntityId not implemented');
};

AbstractEntityTransactionBuilder.getEntityBalance = function() { //abstract BigDecimal getEntityBalance(final E entity);
    alert('AbstractEntityTransactionBuilder.getEntityBalance not implemented');
};

AbstractEntityTransactionBuilder.createTransactionInstance = function() {
//abstract T createTransactionInstance( final E entity, final int date, final BigDecimal amount,
//  final BigDecimal balance, final String detail, final String transType );
    alert('AbstractEntityTransactionBuilder.createTransactionInstance not implemented');
};

AbstractEntityTransactionBuilder.prototype.getEntities = function() {
    if(!this.entities) {
        var entities = {};
        this.entityList.each(function(entity) {
            entities[this.getEntityId(entity)] = entity;
        });
        this.entities = entities;
    }
    return this.entities;
}

AbstractEntityTransactionBuilder.prototype.getEntityBalances = function() {
    if(!this.entityBalances) {
        var entityBalances = {};
        this.entityList.each(function(entity) {
            entityBalances[this.getEntityId(entity)] = this.getEntityBalance(entity);
        });
        this.entityBalances = entityBalances;
    }
    return this.entityBalances;
}

AbstractEntityTransactionBuilder.prototype.getBalanceForEntity = function(entityId) {
    if(!this.entityBalances[entityId]) {
        this.entityBalances[entityId] = 0;
    }
    return this.entityBalances[entityId];
}

AbstractEntityTransactionBuilder.prototype.truncate = function( decimal ) {
    return decimal.toFixed(this.decimalCount);
}

AbstractEntityTransactionBuilder.prototype.addTrans = function(entityId, amount, detail, transType) {
    var balance = this.getBalanceForEntity( entityId );
    if (Ext.Array.contains(this.creditTransTypes, transType)) {
        balance = this.truncate( balance + amount );
    } else {
        balance = this.truncate( balance - amount );
    }
    this.entityBalances[entityId] = balance;
    var trans = this.createTransactionInstance( this.getEntities()[ entityId ], this.date, amount, balance, detail, transType );
    this.transactions.push( trans );
    return trans;
}

function EntityTransactionBuilder(date) {
    this.date = date
}

EntityTransactionBuilder.prototype.getPartyTransBuilder = function(){
    if (!this.partyTransactionBuilder) {
        getDataWithAjax("api/partiesClosingBalance", function(response) {
            this.entityList = Ext.decode(response.responseText);
        });
        this.partyTransactionBuilder = new AbstractEntityTransactionBuilder(this.date, 2,
            [ "CREDIT", "CREDIT_S", "CREDIT_PAY", "CREDIT_INC"]);
        this.partyTransactionBuilder.addSwapTrans = function( debitEntityId, creditEntityId, amount, debitDetail,
                                                              creditDetail, debitTransType,creditTransType) {
            this.addTrans( debitEntityId, amount, debitDetail, debitTransType );
            this.addTrans( creditEntityId, amount, creditDetail, creditTransType );
        }

        this.partyTransactionBuilder.getEntityId = function(entity) {
            return entity["id"];
        }

        this.partyTransactionBuilder.getEntityBalance = function(entity) {
            return entity["balance"];
        }

        this.partyTransactionBuilder.createTransactionInstance = function( entity, date, amount, balance, detail, transType ) {
            return {
                partyId: this.getEntityId(entity),
                amount: amount,
                balance: balance,
                date: date,
                transactionDetail: detail,
                transactionType: transType
            };
        }
    }
    return this.partyTransactionBuilder;
}

EntityTransactionBuilder.prototype.getProdTransBuilder = function(){
    if (!this.productTransactionBuilder) {
        getDataWithAjax("api/productsClosingBalance", function(response) {
            this.entityList = Ext.decode(response.responseText);
        });
        this.productTransactionBuilder = new AbstractEntityTransactionBuilder(this.date, 2, ["RECEIPT", "DIFF"]);

        this.productTransactionBuilder.addTransWithDiscount = function(entityId, amount, discount, transType) {
            var detail = "";
            if ( discount > 0 ) {
                detail = "Discount per unit:" + discount;
            }
            addTrans( entityId, amount, detail, transType );
        }

        this.productTransactionBuilder.addTransWithMargin = function(entityId, amount, margin, transType, detailPrefix) {
            var detail = "";
            var transaction = addTrans( entityId, amount, detail, transType );
            if ( transaction["margin"] !== margin ) {
                transaction['detail'] = detailPrefix + ":Margin updated from " + transaction['detail'] + " to " + margin;
            } else {
                transaction['detail'] = detailPrefix + ":Margin not changed";
            }
            transaction["margin"] = margin;
        }

        this.productTransactionBuilder.getEntityId = function( entity ) {
            return entity['productId'];
        }

        this.productTransactionBuilder.getEntityBalance = function( entity ) {
            return entity['closingStock'];
        }

        this.productTransactionBuilder.createTransactionInstance= function( entity, date, amount, balance, detail, transType ) {
            return {
                productId : entity['productId'],
                quantity : amount,
                balance : balance,
                date: date,
                detail: detail,
                transactionType: transType,
                unitPrice : entity['unitSellingPrice'],
                margin: entity['margin']
            };
        }
    }
    return this.productTransactionBuilder;
}

EntityTransactionBuilder.prototype.getTankTransBuilder = function(){
    if (!this.tankTransactionBuilder) {
        getDataWithAjax("api/tankClosingStock?date=" + this.date, function(response) {
            this.entityList = Ext.decode(response.responseText);
        });
        this.tankTransactionBuilder = new AbstractEntityTransactionBuilder(this.date, 3, ["FILL","TEST", "FILL_S", "DIFF"]);

        this.tankTransactionBuilder.getEntityId = function( entity ) {
            return entity['tankId'];
        }

        this.tankTransactionBuilder.getEntityBalance = function( entity ) {
            return entity['closingStock'];
        }

        this.tankTransactionBuilder.createTransactionInstance = function( entity, date, amount, balance, detail, transType ) {
            return {
                tankId : entity['tankId'],
                quantity : amount,
                balance : balance,
                date : date,
                detail : detail,
                density : 0,
                transType : transType
            };
        }
    }
    return this.tankTransactionBuilder;
}
//TODO Not used, remove
/*EntityTransactionBuilder.prototype.getTankDipStockTransBuilder = function(){
 if (this.dipStockTransactionBuilder == null ) {
 this.dipStockTransactionBuilder = new TankDipStockTransactionBuilder();//  bunkManager.getTankList( date ), date );
 }
 return this.dipStockTransactionBuilder;
 }*/

EntityTransactionBuilder.prototype.getMeterTransactionBuilder = function(){
    if (!this.meterTransactionBuilder) {
        getDataWithAjax("api/activeMeterClosingReading", function(response) {
            this.entityList = Ext.decode(response.responseText);
        });
        this.meterTransactionBuilder = new AbstractEntityTransactionBuilder(this.date, 3, ["SALE"]);
        this.meterTransactionBuilder.getEntityId = function(entity) {
            return entity['meterId'];
        }

        this.meterTransactionBuilder.getEntityBalance = function(entity) {
            return entity['finalReading'];
        }

        this.meterTransactionBuilder.createTransactionInstance = function( entity, date, amount, balance, detail, transType ) {
            return {
                meterId : entity['meterId'],
                meterSale : amount,
                finalReading : balance,
                date: date,
                detail : detail
            };
        }
    }
    return this.meterTransactionBuilder;
}

