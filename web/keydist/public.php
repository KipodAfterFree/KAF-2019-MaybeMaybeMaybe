<?php

include_once __DIR__ . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . "scripts" . DIRECTORY_SEPARATOR . "backend" . DIRECTORY_SEPARATOR . "mmm" . DIRECTORY_SEPARATOR . "api.php";

if (isset($_GET["session"])) {
    $session = $_GET["session"];
    $id = authenticate_session($session)[2];
    if ($id !== null) {
        $key = mmm_load_key($id);
        if ($key !== null)
            echo mmm_derive_key($key);
    } else
        echo "UID Invalid / Failed to load key from /files/mmm/keys/uid.key";
}