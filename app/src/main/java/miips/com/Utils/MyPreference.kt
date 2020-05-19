package miips.com.Utils
//SharedPreferences é uma classe que é utilizada para salvar informações no dispositivo do usuário.
//No projeto, ele é utilizado para guardar o token do servidor no dispositivo.

import android.content.Context

class MyPreference(contex : Context){
    val PREFERENCE_NAME = "SharedPreference"
    val TOKEN = "token"
    val IDCLICK = "idproduct"
    val IDLOCAL = "idlocal"

    /**
     * 1º parâmetro: nome da shared preferences que queremos buscar.
     * 2º parâmetro: modo de acesso (Por defautl utilizamos o MODE_PRIVATE pois ele restringe o acesso apenas para a App que está chamando ou para todas as Apps que tiverem o mesmo user ID).
     */
    val preference = contex.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    /**
     * Para acessar a variável necessita instanciar a classe do SharedPreferences do projeto, no caso a "MyPreference" e acessar o metodo get da variavel armazenada.
     */
    fun getToken(): String? {
        return preference.getString(TOKEN, "")
    }

    /**
     * Para acessar a variável e salvar um valor necessita instanciar a classe do SharedPreferences do projeto, no caso a "MyPreference" e acessar o metodo set.
     */
    fun setTOKEN(token: String){
        preference.edit().putString(TOKEN, token).apply()
    }

    fun getIdClick(): String? {
        return preference.getString(IDCLICK, "")
    }


    fun setIDCLICK(idProduct: String){
        preference.edit().putString(IDCLICK, idProduct).apply()
    }

    fun getIdLocal(): String? {
        return preference.getString(IDLOCAL, "")
    }


    fun setIDLOCAL(idProduct: String){
        preference.edit().putString(IDLOCAL, idProduct).apply()
    }

}