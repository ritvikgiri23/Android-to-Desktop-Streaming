const {app, BrowserWindow} = require('electron')
const {PythonShell} = require('python-shell')
const url = require("url");
const path = require("path");


    function createWindow () {
      mainWindow = new BrowserWindow({
        width: 1920,
        height: 1080,
        autoHideMenuBar: true,
        webPreferences: {
          nodeIntegration: true
        }
      })

      mainWindow.loadURL(
        url.format({
          pathname: path.join(__dirname, `dist/streamera/index.html`),
          protocol: "file:",
          slashes: true
        })
      );
      // Open the DevTools.
      //mainWindow.webContents.openDevTools()
      mainWindow.maximize()

      
      

      mainWindow.webContents.on('did-fail-load', () => {
        console.log('did-fail-load');
        mainWindow.loadURL(url.format({
          pathname: path.join(__dirname, 'dist/streamera/index.html'),
          protocol: 'file:',
          slashes: true
        }));
        // REDIRECT TO FIRST WEBPAGE AGAIN
          });

      mainWindow.on('closed', function () {
        mainWindow = null
      })
    }

    app.on('ready', function() {
      // call python?
      //var subpy = require('child_process').spawn('python', ['resources/app/backend_belt_jsw_salem/backend_belt_insp_sway_detection.py']);
      // var subpy = require('child_process').spawn('python', ['Backend/new/client_seek_csi_main.py']);

      let pyshel = new PythonShell('resources/app/Backend/new/client_seek_csi_main.py',  function  (err, results)  {
      if  (err)  throw err;
      console.log('client_seek_csi_main.py finished.');
      console.log('results: ----->>', results);
      });

      function sleep(milliseconds) {
        const date = Date.now();
        let currentDate = null;
        do {
          currentDate = Date.now();
        } while (currentDate - date < milliseconds);
      }
    
      sleep(1000);
      createWindow();
    });

var mainWindow = null;

app.on('window-all-closed', function() {
  //if (process.platform != 'darwin') {
    app.quit();
  //}
});

