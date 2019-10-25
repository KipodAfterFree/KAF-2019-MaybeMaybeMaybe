function load() {
    page("home");
    console.log("------------------------------------USER INFO------------------------------------");
    console.log("     Mommy signs using the hmac functions ontop of the sha256 hash function.     ");
}

function sign() {
    api("scripts/backend/mmm/mmm.php", "mmm", "sign", {data: get("document").value},(success, result, error)=>{
        if (success)
            download("paper.mmm", result);
    }, authenticate());
}