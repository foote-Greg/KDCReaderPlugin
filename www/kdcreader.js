

var kdcreader =  {
    createConnection: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'KDCPlugin', // mapped to our native Java class called "KDCReaderPlugin"
            'listenForKDC',
            null
        );
    },
    disableConnection: function (successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'KDCPlugin', // mapped to our native Java class called "KDCReaderPlugin"
            'disableKDC',
            null
        );
    }
};
module.exports = kdcreader;