Ext.require('Ext.container.Viewport');

var bunkCache = {};
var pageMask;
var graphColorArray = ['#1ABC9C', '#F1C40F', '#3498DB', '#C0392B', '#9B59B6', "#48C9B0", "#16A085", "#3498DB", "#9B59B6", "#E67E22", "#2ECC71", "#7F8C8D", "#F1C40F", "#E74C3C", "#273746"]; //10 colors to supports twenty fields

function getDataWithAjax(urlPath, success, failure) {
    Ext.Ajax.request({
        url: urlPath,
        success: success,
        failure: (failure ? failure : function (form, action) {
            console.log('failed to get response for ' + urlPath);
        })
    });
}

function showFailedMask() {
    if (!pageMask) {
        pageMask = new Ext.LoadMask(Ext.getBody(), {msg: "Failed to read data from server. Please try after sometime."});
    }
    pageMask.show();
}

function showFailedMask(msg) {
    if (!pageMask) {
        pageMask = new Ext.LoadMask(Ext.getBody(), {msg: "Failed to read data from server: " + msg +". Please try after sometime."});
    }
    pageMask.show();
}

function hideFailedMask() {
    if (pageMask) {
        pageMask.hide();
    }
}


var navStore = Ext.create('Ext.data.TreeStore', {
    root: {
        expanded: true,
        children: [
            {
                id: 'summary', text: "Summary", collapsible: false, expanded: true, panelbuilder: 'buildSummaryPanel',
                children: [
                    {
                        id: 'reports',
                        text: "Statements/Reports",
                        leaf: true,
                        collapsible: false,
                        panelbuilder: 'buildReportsPanel'
                    }
                ]
            }, {
                id: 'daily-statement',
                text: "Daily statement",
                collapsible: false,
                leaf: true,
                panelbuilder: 'buildDailyStatementPanel'
            }, {
                id: 'add-edit',
                text: "Add/Edit",
                collapsible: false,
                leaf: false,
                expanded: true,
                panelbuilder: 'showPartyViewer',
                children: getAddEditItems()
            }, {
                id: 'products', text: "Products", expanded: true, collapsible: false,
                children: [
                    {id: 'prods-rate-change', text: "Change price", leaf: true, panelbuilder: 'buildRateChangePanel'},
                    {
                        id: 'receive-prods',
                        text: "Receive products",
                        expanded: true,
                        collapsible: false,
                        children: [
                            {
                                id: 'prods-receive-petrol-diesel',
                                text: "Petrol/Diesel",
                                leaf: true,
                                panelbuilder: 'buildFuelReceiptsPanel'
                            },
                            {
                                id: 'prods-receive-lubes',
                                text: "Lubes",
                                leaf: true,
                                panelbuilder: 'buildLubesReceiptsPanel'
                            },
                            {
                                id: 'prods-receive-others',
                                text: "Battery water/Others",
                                leaf: true,
                                panelbuilder: 'buildBatteryWaterReceiptsPanel'
                            }
                        ]
                    }
                ]
            }/*,{
             id:'parties', text: "Parties", expanded: true, collapsible: false, children: [
             { id:'parties-add-edit', text: "Add/Edit", leaf: true }
             ]
             }*/
        ]
    }
});

Ext.override(Ext.data.Connection, {
    timeout: 300000
});

Ext.application({
    name: 'bronz.bunk',
    extend: 'Ext.app.Application',
    launch: function () {
        Ext.getBody().setStyle('overflow', 'auto');
        var myMask = new Ext.LoadMask(Ext.getBody(), {msg: "Loading..."});
        myMask.show();
        loadDateAndUserInfo(myMask);



        Ext.create('Ext.container.Viewport', {
            layout: 'border',
            defaults: {
                bodyStyle: 'padding:0px'
            },
            items: [{
                //title: 'Header',
                region: 'north',
                header: false,
                margins: '0 0 0 0',
                cmargins: '0 0 0 0',
                height: 60,
                minSize: 60,
                maxSize: 60,
                resize: false,
                border: 0,
                layout: 'border',
                items: [{
                    //title: 'Title',
                    preventBodyReset: true,
                    html: '<h1>Century bunk mgmt software</h1>',
                    width: '80%',
                    region: 'west',
                    height: '100%',
                    flex: 2,
                    margins: '0 0 0 0',
                    cmargins: '0 0 0 0'

                }, {
                    id: 'userInfo',
                    html: '<h4>Loading... </h4>',
                    preventBodyReset: true,
                    width: '20%',
                    region: 'east',
                    height: '100%',
                    margins: '0 0 0 0',
                    cmargins: '0 0 0 0'
                }]
            }, {
                id: 'maincontent',
                html: 'Welcome',
                region: 'center',
                layout: 'fit',
                margins: '0 0 0 0',
                width: '100%',
                height: '100%',
                autoScroll: true,
                items: []
            }, {
                //title: 'NavPanel',
                header: false,
                region: 'west',
                margins: '0 0 0 0',
                cmargins: '0 0 0 0',
                padding: '0px 0px 0px 0px',
                width: 200,
                minWidth: 150,
                maxWidth: 250,
                resize: true,
                xtype: 'treepanel',
                rootVisible: false,
                // Sharing the store synchronizes the views:
                store: navStore,
                lines: false,
                collapsible: false,
                useArrows: true,
                listeners: {
                    beforecollapse: function (n) {
                        return false;
                    },
                    itemclick: function (view, record, item, index, evt, eOpts) {
                        if (!record.raw.panelbuilder) {
                            return;
                        }
                        updateSubPanel(record);
                        return false;
                    }
                }
            }]
        });
    }
});

function updateSubPanel(record) {
    var menuId = record.get('id');
    var newPanel;
    if (record.raw.panelbuilder) {
        newPanel = window[record.raw.panelbuilder](getTitle(record), record);
    } else {
        newPanel = new Ext.Panel({
            html: 'Success' + menuId
        });
    }
    if (newPanel) {
        var mainPanel = Ext.getCmp('maincontent');
        mainPanel.update('');//Clear current contents
        mainPanel.removeAll(); //remove all the items

        mainPanel.add(newPanel);
        mainPanel.doLayout();
        newPanel.doLayout();
    }
}

function updateSummaryPanel() {
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg: "Loading..."});
    myMask.show();
    var mainPanel = Ext.getCmp('maincontent');
    mainPanel.update('');//Clear current contents
    mainPanel.removeAll(); //remove all the items

    mainPanel.add(buildSummaryPanel());
    mainPanel.doLayout();
    myMask.hide();
}

function buildSummaryPanel() {
    var summaryPanel = Ext.create('Ext.form.Panel', {
        id: 'summaryPanel',
        //title: "Summary",
        frame: true,
        autoScroll: true,
        bodyStyle: 'padding:0px 0px 0',
        width: '100%',
        autoHieght: true,
        layout: 'column',
        defaults: {
            anchor: '100%'
        },
        //html:'<h4>Summary</h4>'
        items: []

    });

    addReportPanel("api/result/fuelsSalesSummaryV2", {"start" : (bunkCache.infos.todayDate - 30), "end" : bunkCache.infos.todayDate}, "Fuels sale summary (Last 30 days)", summaryPanel , ' L', 1, "chart");
    addReportPanel("api/result/fuelsTestSummary", {"start" : (bunkCache.infos.todayDate - 30), "end" : bunkCache.infos.todayDate}, "Fuels test summary (Last 30 days)", summaryPanel , ' L', 0.5, "chart");
    addReportPanel("api/result/rateChangeHistory", {"start" : (bunkCache.infos.todayDate - 30), "end" : bunkCache.infos.todayDate}, "Rate change history (Last 30 days)", summaryPanel , ' Rs', 0.5, "table");

    return summaryPanel;
}

function getTitle(record) {
    var node = record;
    var title = '';
    do {
        if (title.length > 0) {
            title = node.get('text') + ' -> ' + title;
        } else {
            title = node.get('text');
        }
        node = node.parentNode;
    } while (node.parentNode)
    return title;
}

function prepareErrorMsg(msg, errors) {
    var message = '<b>' + msg + '</b><br/><ul style="list-style-type:square">';
    errors.forEach(function (item) {
        message = message + '<li>' + item + '</li>';
    });
    message = message + '</ul>';
    return message;
}

function editableColumnRenderer(value, metaData, record, rowIndex, colIndex, store, view) {
    if (record.data.notEditable !== true) {
        metaData.style = 'background:#ffffe5 !important;';
    }
    return value;
}

function numberFieldConfig() {
    return {
        xtype: 'numberfield',
        allowNegative: false,
        allowBlank: false,
        minValue: 0,
        // Remove spinner buttons, and arrow key and mouse wheel listeners
        hideTrigger: true,
        keyNavEnabled: false,
        mouseWheelEnabled: false
    };
}

function textFieldConfig() {
    return {
        allowBlank: false
    };
}

function isNumber(o) {
    return !isNaN(o - 0) && o !== null && o !== "" && o !== false;
}

function loadDateAndUserInfo(myMask) {
    getDataWithAjax('api/info', function (response) {
        bunkCache.infos = Ext.decode(response.responseText);
        if (Ext.getCmp('userInfo')) {
            Ext.getCmp('userInfo').body.update('<h4>Username:' + (bunkCache.infos ? bunkCache.infos.username : 'no-user') + '<br/>Date:' + (bunkCache.infos ? bunkCache.infos.todayDateDayText : '-') + '</h4>');
        }
        updateSummaryPanel();
        myMask.hide();
    }, function (response) {
        console.log('failed to read info from server:' + response.responseText);
        myMask.hide();
        showFailedMask(response.responseText);
    });
}


function checkErrorsWarningsAndProceed(errors, warnings, callback) {
    if (errors.length > 0) {
        Ext.MessageBox.alert('Error', prepareErrorMsg("Please fix the below validation errors.", errors));
        return;
    }
    if (errors.length == 0 && warnings.length == 0) {
        callback();
    }

    if (warnings.length > 0) {
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
            fn: function (btn) {
                if (btn == "ok") {
                    callback();
                }
            }
        });
    }
    return true;
}

// parse a date in 13-12-2015 dd-mm-yyyy format
function parseDateText(input) {
    var parts = input.split('-');
    // new Date(year, month [, day [, hours[, minutes[, seconds[, ms]]]]])
    return new Date(parts[2], parts[1] - 1, parts[0]); // Note: months are 0-based
}

function dateToSimpleText(date) {
    var day = date.getDate();
    var month = date.getMonth() + 1;
    var year = date.getFullYear();
    return padLeft(day, 2) + '-' + padLeft(month, 2) + '-' + year;
}

function padLeft(nr, n, str) {
    return Array(n - String(nr).length + 1).join(str || '0') + nr;
}

Date.prototype.ddmmyyyy = function () {
    var yyyy = this.getFullYear();
    var mm = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this.getMonth() + 1); // getMonth() is zero-based
    var dd = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
    return "".concat(dd).concat(mm).concat(yyyy);
};

Date.prototype.ddmmyyyyhhmm = function () {
    var yyyy = this.getFullYear();
    var mm = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this.getMonth() + 1); // getMonth() is zero-based
    var dd = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
    var hh = this.getHours() < 10 ? "0" + this.getHours() : this.getHours();
    var min = this.getMinutes() < 10 ? "0" + this.getMinutes() : this.getMinutes();
    return "".concat(dd).concat(mm).concat(yyyy).concat(hh).concat(min);
};

Date.prototype.ddmmyyyyhhmmss = function () {
    var yyyy = this.getFullYear();
    var mm = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this.getMonth() + 1); // getMonth() is zero-based
    var dd = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
    var hh = this.getHours() < 10 ? "0" + this.getHours() : this.getHours();
    var min = this.getMinutes() < 10 ? "0" + this.getMinutes() : this.getMinutes();
    var ss = this.getSeconds() < 10 ? "0" + this.getSeconds() : this.getSeconds();
    return "".concat(dd).concat('-').concat(mm).concat('-').concat(yyyy).concat(' ').concat(hh).concat(':').concat(min).concat(':').concat(ss);
};

function paramsToQuery(data) {
    return Object.keys(data).map(function(key) {
        return [key, data[key]].map(encodeURIComponent).join("=");
    }).join("&");
}


function getRowActionsColumns(modelName, callback) {
    return {
        xtype:'actioncolumn',
            width:50,
        items: [{
        icon: 'assets/add-row.gif',  // Use a URL in the icon config
        tooltip: 'Insert row below',
        handler: function(grid, rowIndex, colIndex) {
            var store = grid.getStore();
            store.insert(rowIndex + 1, Ext.create(modelName));
            callback();
        }
    },{
        icon: 'assets/rm-row.png',
        tooltip: 'Delete row',
        handler: function(grid, rowIndex, colIndex) {
            var store = grid.getStore();
            var rec = store.getAt(rowIndex);

            //Disable the removal of saved transactions
            if(rec && rec.data.notEditable == true) {
                Ext.MessageBox.alert('Stop', "This transaction cannot be removed.");
                return;
            }

            store.removeAt(rowIndex);
            if(store.count() <=0 ) {
                //Add an empty row if the store becomes empty after removing a row.
                store.add(Ext.create(modelName));
            }
            callback();
        }
    }]
    }
}

function isBeta() {
    var getParams = document.URL.split("?");
    if (getParams.length<2) {
        return false;
    }
    // transforming the GET parameters into a dictionnary
    var params = Ext.urlDecode(getParams[getParams.length - 1]);
    if (params["beta"] === "1") {
        return true;
    }
    return false;
}


function getAddEditItems() {
    var items = [
        {id: 'add-edit-parties', text: "Credit Parties/Employees", leaf: true, panelbuilder: 'showPartyViewer'}
    ];
    if (isBeta()) {
        items.push({id: 'admin-panel', text: "Admin Beta", leaf: true, panelbuilder: 'showAdminPanel'})
    }
    return items;
}


