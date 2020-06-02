package org.chickenhook.binderfuzzy.reflectionbrowser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.fuzzcreator.FuzzCreator
import org.chickenhook.binderfuzzy.reflectionbrowser.impl.BrowserImpl
import org.chickenhook.binderfuzzy.reflectionbrowser.ui.classobjectfragment.ClassObjectFragment
import org.chickenhook.binderfuzzy.reflectionbrowser.ui.classobjectfragment.items.ClassMemberItem
import org.chickenhook.binderfuzzy.reflectionbrowser.ui.serviceselector.ServiceSelectorFragment
import org.chickenhook.binderfuzzy.reflectionbrowser.ui.serviceselector.items.ClassItem
import java.lang.reflect.Field
import java.lang.reflect.Method

class ReflectionBrowser : AppCompatActivity(),
    ServiceSelectorFragment.OnListFragmentInteractionListener,
    ClassObjectFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reflection_browser_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ServiceSelectorFragment.newInstance())
                .addToBackStack(""+this)
                .commit()
        }
    }

    override fun onListFragmentInteraction(item: ClassItem?) {
        item?.let {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    ClassObjectFragment.newInstance(BrowserImpl.newObject(item.obj))
                )
                .addToBackStack(""+item)
                .commit()
        }
    }

    override fun onListFragmentInteraction(item: ClassMemberItem?) {
        item?.let {

            when (it.member) {
                is Field -> {
                    // todo cover with if else
                    /*val i = Intent()
                        //i.putExtra(BROWSER_RESULT, ) // todo fetch value and set as extra if serializable
                        setResult(RESULT_OK, i)
                        finish()*/
                    (item.member as Field).get(item.host)?.let { value ->
                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.container,
                                ClassObjectFragment.newInstance(
                                    BrowserImpl.newObject(value)
                                )
                            )
                            .addToBackStack(""+item)
                            .commit()
                    }

                }
                is Method -> {
                    // todo start fuzz creator
                    val startIntent = Intent(this, FuzzCreator::class.java)
                    startIntent.putExtra(FuzzCreator.ARG_METHOD_NAME, it.member.toGenericString())
                    startIntent.putExtra(FuzzCreator.ARG_CLASS_NAME, it.member.declaringClass)
                    startIntent.putExtra(FuzzCreator.ARG_HOST_ID, BrowserImpl.newObject(it.host))
                    startActivity(startIntent)
                }
                else -> {
                    // todo log!

                }
            }

        }
    }

    companion object {
        const val BROWSER_RESULT = "reflection_browser_result"
    }
}
