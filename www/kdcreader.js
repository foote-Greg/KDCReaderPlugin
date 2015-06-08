

var kdcreader =  {
    createConnection: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'KDCPlugin', // mapped to our native Java class called "KDCReaderPlugin"
            'listenForKDC',
            [{'nada':'nothing'}]
        );
    },
    disableNFCPower: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'KDCPlugin', // mapped to our native Java class called "KDCReaderPlugin"
            'disableKDC',
            [{'nada':'nothing'}]
        );
    },
     enableNFCPower: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'KDCPlugin', // mapped to our native Java class called "KDCReaderPlugin"
            'enableKDC',
            [{'nada':'nothing'}]
        );
    }
};
module.exports = kdcreader;