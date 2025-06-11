import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelTransaksiTambahan(
    val idTambahan: String,
    val namatambahan: String,
    val hargatambahan: String
) : Parcelable
