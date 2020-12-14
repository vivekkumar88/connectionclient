# connectionclient
- HTTP and MQTT client for Android ready to use
- Only HTTP support as of now. MQTT is in progress.
- Android HTTP Connection used for making connection.
- Language is in Java

# How to use
# Module Import setup
- Checkout this code in any folder.
- Do import Module from your project and locate this checkout source code and select "connectioncall" folder.
- Check settings.gradle, below line should be there after import module, if this is missing then please included this.
  include ':conectioncall'
- Check app level build.gradle file, please add below code under dependencies (if already added then please ignore)
implementation project(':conectioncall')
# Call api from your code
- Modify environment (res/raw/environment) file and add your host url and api path. Please fallow sample code (MainActivity) and sample environment file.
- Add your API class under connectioncall->src->main->java->com.example.conectioncall->call->apicalls. For each API you have to write one class, the name of class is same as which you define in environment file. Example testapicall.path.
- Please check MainActivity class.
  - extend your MainActivity class with IPlatformResourceDelegate, IServiceDelegate interface.
  - Copy and past below code under MainActivity class
  
  // To call connectioncall APIs // Define to check network status
    private var mNetworkStatus: INetworkStatus? = null
    // To call connectioncall APIs
    
  // To call connectioncall APIs //  call below method.
     initializePlatformResources()
     Service.sharedInstance().initializeServiceEnvironment(applicationContext, R.raw.environment)
  // To call connectioncall APIs
        
  // To call connectioncall APIs // Past all below functions.
    override fun getValueStore(): IValueStore? {
        return AppData.getInstance().valueStore()
    }

    override fun getSecureStore(): ISecureStore? {
        return AppData.getInstance().secureStore()
    }

    override fun getNetworkStatus(): INetworkStatus? {
        return mNetworkStatus
    }

    override fun onEvent(event: Service.Event?, info: HashMap<String, Any>?) {
        TODO("Not yet implemented")
    }

    private fun initializePlatformResources() {
        if (mNetworkStatus == null) {
            mNetworkStatus =
                NetworkStatus(applicationContext)
        }
        Service.sharedInstance().setPlatformResourceDelegate(this)
    }// To call connectioncall APIs
    override fun getValueStore(): IValueStore? {
        return AppData.getInstance().valueStore()
    }

    override fun getSecureStore(): ISecureStore? {
        return AppData.getInstance().secureStore()
    }

    override fun getNetworkStatus(): INetworkStatus? {
        return mNetworkStatus
    }

    override fun onEvent(event: Service.Event?, info: HashMap<String, Any>?) {
        TODO("Not yet implemented")
    }

    private fun initializePlatformResources() {
        if (mNetworkStatus == null) {
            mNetworkStatus =
                NetworkStatus(applicationContext)
        }
        Service.sharedInstance().setPlatformResourceDelegate(this)
    }

# Other uses 
- You can use offline debugging using mIsDevMode flag true.
- Common Http error handled.


