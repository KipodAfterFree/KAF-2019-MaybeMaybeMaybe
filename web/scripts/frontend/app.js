function load() {
    page("home");
}

function sign() {
    api("scripts/backend/mmm/mmm.php", "mmm", "sign", {data: get("document").value},(success, result, error)=>{
        if (success)
            download("paper.sodium-signed", result);
    }, authenticate());
}