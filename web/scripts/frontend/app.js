function load() {
    page("home");
    console.log("------------------------------------USER INFO------------------------------------");
    console.log("     Mommy signs using the hmac function ontop of the sha256 hash function.     ");
    console.log("                    Use 'nc ctf.kaf.sh 1101' to upload files.                   ");
}

function sign() {
    api("scripts/backend/mmm/mmm.php", "mmm", "sign", {data: get("document").value}, (success, result, error) => {
        if (success)
            download("paper.mmm", result);
    }, authenticate());
}

function verify() {
    page("results");
    fetch("keydist/public.php?session=" + authenticate_cookie_pull(AUTHENTICATE_SESSION_COOKIE), {
        method: "get"
    }).then(response => {
        response.text().then((result) => {
            result = mmm_verify(get('data').value, result);
            get('pubkey').classList.add("auth-" + result);
            get('pubkey').innerText = "PubDigest verification: " + (result ? "OK" : "Fail");
        });
    });
    api("scripts/backend/mmm/mmm.php", "mmm", "verify", {data: get("data").value}, (success, result) => {
        get('prikey').classList.add("auth-" + result);
        get('prikey').innerText = "PriDigest verification: " + (result ? "OK" : "Fail");
    }, authenticate());
}

function mmm_verify(mmm, pubkey) {
    let parsed = mmm_parse(mmm);
    return CryptoJS.HmacSHA256(parsed[2], pubkey).toString(CryptoJS.enc.Hex) === parsed[1];
}

function mmm_parse(mmm) {
    let lines = mmm.split("\n");
    if (lines.length >= 7) {
        if (lines[0] === "---BEGIN MMM---" &&
            lines[1] === "---PRIVATE DIGEST---" &&
            lines[3] === "---PUBLIC DIGEST---" &&
            lines[5] === "---MESSAGE---" &&
            lines[lines.length - 1] === "---END MMM---") {
            let private_hmac = lines[2];
            let public_hmac = lines[4];
            let data = "";
            for (let l = 6; l < lines.length - 1; l++) {
                if (data.length !== 0) {
                    data += "\n";
                }
                data += lines[l];
            }
            return [private_hmac, public_hmac, data];
        }
    }
    return [];
}