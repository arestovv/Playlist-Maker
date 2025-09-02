import com.arestov.playlistmaker.mvvm.search.domain.repository.PreferencesStorage

class GetPreferencesStorageUseCase(
    private val preferencesStorage: PreferencesStorage
) : PreferencesStorage {

    override fun putString(value: String) {
        preferencesStorage.putString(value)
    }

    override fun getString(defaultValue: String?): String? {
        return preferencesStorage.getString(defaultValue)
    }

    override fun putInt(value: Int) {
        preferencesStorage.putInt(value)
    }

    override fun getInt(defaultValue: Int): Int {
        return preferencesStorage.getInt(defaultValue)
    }

    override fun putBoolean(value: Boolean) {
        preferencesStorage.putBoolean(value)
    }

    override fun getBoolean(defaultValue: Boolean): Boolean {
        return preferencesStorage.getBoolean(defaultValue)
    }

    override fun remove() {
        preferencesStorage.remove()
    }

    override fun clear() {
        preferencesStorage.clear()
    }

    override fun contains(key: String): Boolean {
       return preferencesStorage.contains(key)
    }
}