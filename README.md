# GDPR_Kotlin

#Example

class MainActivity: Activity(), GDPRRequestable {
    override val requestingActivity: Activity
        get() = this

    override fun onRequestGDPRCompleted(error: FormError?) {
        if (error == null) {
            // did Request GDPR
            // start loading ad
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        
        // Your code
        
        requestGDPR()
    }
}
