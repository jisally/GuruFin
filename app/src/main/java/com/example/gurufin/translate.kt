package com.example.gurufin

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import android.widget.ArrayAdapter;



class translate : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         StrictMode.enableDefaults()
        val names = arrayOf("한국어", "영어", "일본어", "중국어 (간체)", "중국어 (번체)", "스페인어", "프랑스어", "베트남어", "태국어", "인도네시아어");
        val codes = arrayOf("ko", "en", "ja", "zh-CN", "zh-TW", "es", "fr", "vi", "th", "id");
        val langs = arrayOf(0, 0)

        val layout = LinearLayout(this)
        layout.orientation = 1

        val lay1 = LinearLayout(this)
        val lay2 = LinearLayout(this)
        val txt1 = TextView(this)
        val txt2 = EditText(this)
        val trans = Button(this)
        val txt3 = TextView(this)
        val txt4 = EditText(this)

        txt1.text = "입력 언어 : "
        txt1.textSize = 19f
        txt1.setTextColor(Color.BLACK)
        lay1.addView(txt1)
        val spin1 = Spinner(this)
        spin1.adapter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, names)
        spin1.layoutParams = LinearLayout.LayoutParams(-1, -2)
        spin1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                langs[0] = pos
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        lay1.addView(spin1)
        layout.addView(lay1)

        txt2.hint = "번역할 내용 입력..."
        txt2.setTextColor(Color.BLACK)
        txt2.setHintTextColor(Color.GRAY)
        layout.addView(txt2)

        val blank = TextView(this)
        blank.text = " "
        blank.textSize = 15f
        layout.addView(blank)

        txt3.text = "출력 언어 : "
        txt3.textSize = 19f
        txt3.setTextColor(Color.BLACK)
        lay2.addView(txt3)
        val spin2 = Spinner(this)
        spin2.adapter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, names)
        spin2.layoutParams = LinearLayout.LayoutParams(-1, -2)
        spin2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                langs[1] = pos
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        lay2.addView(spin2)
        layout.addView(lay2)

        txt4.hint = "번역 결과..."
        txt4.setTextColor(Color.BLACK)
        txt4.setHintTextColor(Color.GRAY)
        layout.addView(txt4)

        trans.text = "번역"
        trans.setBackgroundColor(Color.rgb(131,36,255))
        trans.setTextColor(Color.WHITE)
        trans.setTextSize(19f)
        trans.setOnClickListener {
            val input = txt2.text.toString().trim()
            if (input == "") {
                toast("번역할 내용을 입력되지 않았습니다.")
            } else {
                val output = translate(codes[langs[0]], codes[langs[1]], input)
                if (output.equals("")) {
                    toast("번역 실패")
                } else {
                    txt4.setText(output)
                    toast("번역 결과 : " + output)
                }
            }
        }
        layout.addView(trans)

        val pad = dip2px(20)
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }
    
    //네이버 파파고 번역 API 연동
    private fun translate(lang1: String, lang2: String, value: String): String {
        try {
            val url = URL("https://openapi.naver.com/v1/papago/n2mt")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.connectTimeout = 5000
            con.doOutput = true
            con.setRequestProperty("X-Naver-Client-Id", "zDnrALmdNiQMJwZcV0rU") //고유한 ID와 key를 할당받아서 사용
            con.setRequestProperty("X-Naver-Client-Secret", "iCy1TVsUPm")
            val dos = DataOutputStream(con.outputStream)
            dos.write(("source=" + lang1 + "&target=" + lang2 + "&text=" + URLEncoder.encode(value, "UTF-8")).toByteArray(
                StandardCharsets.UTF_8))
            dos.flush()
            val dis = DataInputStream(con.inputStream)
            val br = BufferedReader(InputStreamReader(dis))
            val res = br.readLine()
            if (res == "") return "";
            val data = JSONObject(res)
            return data.getJSONObject("message").getJSONObject("result").getString("translatedText")
        } catch (e: Exception) {
//            toast(e.toString())
        }
        return ""
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun dip2px(dips: Int): Int {
        return Math.ceil((dips * this.resources.displayMetrics.density).toDouble()).toInt()
    }
    
    //메뉴 
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.targetMain ->{
                val intent =Intent(this,TargetMain::class.java)
                startActivity(intent)
                return true
            }
            R.id.translate ->{
                val intent =Intent(this,translate::class.java)
                startActivity(intent)
                return true
            }
            R.id.calendar ->{
                val intent =Intent(this,calendar::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

