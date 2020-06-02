package org.chickenhook.binderfuzzy.reflectionbrowser.impl

import android.content.Context
import android.util.Log
import java.lang.reflect.Field
import java.lang.reflect.Member

class BrowserImpl {


    companion object {

        const val TAG = "BrowserImpl"

        fun getServices(context: Context): ArrayList<Class<out Any>> {
            val serviceList = ArrayList<Class<out Any>>()
            Context::class.java.declaredFields.forEach {
                it.isAccessible = true
                val value = it.get(null)
                value?.let {
                    if (it is String) {
                        try {
                            val service = context.getSystemService(it)
                            serviceList.add(service::class.java as Class<Any>)
                        } catch (exception: Exception) {
                            Log.e(TAG, "Error while fetch service ", exception)
                        }
                    }
                }
            }
            serviceList.add(context.packageManager::class.java)
            return serviceList
        }

        fun getServiceInstances(context: Context): ArrayList<Any> {
            val serviceList = ArrayList<Any>()
            Context::class.java.declaredFields.forEach {
                it.isAccessible = true
                val value = it.get(null)
                value?.let {
                    if (it is String) {
                        try {
                            val service = context.getSystemService(it)
                            serviceList.add(service)
                        } catch (exception: Exception) {
                            Log.e(TAG, "Error while fetch service ", exception)
                        }
                    }
                }
            }
            serviceList.add(context.packageManager)
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val sCurrentActivityThread =
                activityThreadClass.getDeclaredField("sCurrentActivityThread")
            sCurrentActivityThread.isAccessible = true
            sCurrentActivityThread.get(null)?.let {
                serviceList.add(it)
            }
            return serviceList
        }

        fun getService(context: Context, serviceClass: Class<Any>): Any? {
            val service = context.getSystemService(serviceClass)
            return service
        }

        fun getValuesRecursive(
            obj: Any,
            maxDepth: Int = 3,
            values: HashSet<Any> = HashSet()
        ): HashSet<Any> {
            if (maxDepth == 0) {
                return values
            }
            getMembers(obj::class.java).forEach {
                if (it is Field) {
                    it.isAccessible = true
                    it.get(obj)?.let {
                        values.add(it)
                        getValuesRecursive(it, maxDepth - 1, values)
                    }
                }
            }
            return values
        }

        fun getMembers(clazz: Class<out Any>): ArrayList<Member> {
            return getMembersIncludingSuper(clazz)
        }

        private fun getMembersIncludingSuper(clazz: Class<out Any>): ArrayList<Member> {
            val members = ArrayList<Member>()
            members.addAll(clazz.declaredFields)
            members.addAll(clazz.fields)
            members.addAll(clazz.methods)
            members.addAll(clazz.declaredMethods)
            clazz.superclass?.let {
                members.addAll(getMembersIncludingSuper(it))
            }
            return members
        }

        fun <K> getValuesOfType(
            objectToSearchIn: Any,
            clazzToSearchIn: Class<out Any> = objectToSearchIn::class.java,
            typeToSearchFor: Class<K>
        ): ArrayList<K> {
            val list = ArrayList<K>()
            clazzToSearchIn.fields.forEach {
                if (it.type == typeToSearchFor || typeToSearchFor.isAssignableFrom(it.type)) {
                    it.isAccessible = true
                    val obj = it.get(objectToSearchIn)
                    if (obj != null) {
                        list.add(obj as K)
                    }
                }
            }
            clazzToSearchIn.declaredFields.forEach {
                if (it.type == typeToSearchFor || typeToSearchFor.isAssignableFrom(it.type)) {
                    it.isAccessible = true
                    val obj = it.get(objectToSearchIn)
                    if (obj != null) {
                        list.add(obj as K)
                    }
                }
            }
            return list
        }


        private val browserObjects = HashMap<Int, Any>()

        var currId = 0

        fun newObject(obj: Any): Int {
            val id = currId
            browserObjects.put(id, obj)
            currId++
            return id
        }

        fun getObjectById(id: Int): Any? {
            return browserObjects[id]
        }
    }
}