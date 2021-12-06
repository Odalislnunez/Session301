package es.usj.mastertsa.onunez.session301

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import es.usj.mastertsa.onunez.session301.databinding.ActivityThirdBinding

const val PAGES : Int = 3

class ThirdActivity : AppCompatActivity() {
    var firstPage : LinearLayout? = null
    var secondPage : LinearLayout? = null
    var thirdPage : LinearLayout? = null
    private lateinit var bindings : ActivityThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        bindings.viewPager.adapter = AdminPageAdapter(this)
        bindings.btnFirstPage.setOnClickListener {
            bindings.viewPager.currentItem = 0
        }
        bindings.btnSecondPage.setOnClickListener {
            bindings.viewPager.currentItem = 1
        }
        bindings.btnThirdPage.setOnClickListener {
            bindings.viewPager.currentItem = 2
        }
    }

    fun loadNewspapers() {
        val newspapers = arrayOf("elmundo.es", "elpais.es",
            "abc.com")
        val newspapersList =
            secondPage!!.findViewById<ListView>(R.id.secondListView)
        newspapersList.adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, newspapers)
        newspapersList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://${newspapers[position]}"))
                startActivity(intent)
            }
    }
    fun loadBrowsers() {
        val browsers = arrayOf("google.es", "yahoo.es", "bing.com")
        val browserList =
            firstPage!!.findViewById<ListView>(R.id.firstListView)
        browserList.adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, browsers)
        browserList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://${browsers[position]}"))
                startActivity(intent)
            }
    }
}

class AdminPageAdapter(private val activity: ThirdActivity) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any):
            Boolean {
        return view == `object`
    }
    override fun instantiateItem(container: ViewGroup, position:
    Int): Any {
        val currentPage : View?
        when(position) {
            0 -> {
                if(activity.firstPage == null)
                    activity.firstPage =
                        LayoutInflater.from(activity).inflate(R.layout.first_page, null)
                                as LinearLayout
                activity.loadBrowsers()
                currentPage = activity.firstPage
            }
            1 -> {
                if(activity.secondPage == null)
                    activity.secondPage =
                        LayoutInflater.from(activity).inflate(R.layout.second_page,
                            null) as LinearLayout
                activity.loadNewspapers()
                currentPage = activity.secondPage
            }
            else -> {
                if(activity.thirdPage == null)
                    activity.thirdPage =
                        LayoutInflater.from(activity).inflate(R.layout.third_page, null)
                                as LinearLayout
                currentPage = activity.thirdPage
            }
        }
        (container as ViewPager).addView(currentPage, 0)
        return currentPage!!
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
    override fun getCount(): Int {
        return PAGES
    }
}