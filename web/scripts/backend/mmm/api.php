<?php
include_once __DIR__ . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . "base" . DIRECTORY_SEPARATOR . "api.php";
include_once __DIR__ . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . "authenticate" . DIRECTORY_SEPARATOR . "api.php";

const KEYPAIR_DIR = __DIR__ . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . "files" . DIRECTORY_SEPARATOR . "mmm" . DIRECTORY_SEPARATOR . "keys";

function mmm()
{
    api("mmm", function ($action, $parameters) {
        $userID = authenticate();
        if ($userID !== null) {
            mmm_prepare($userID);
            if ($action === "sign") {
                if (isset($parameters->data)) {
                    return [true, base64_encode(sodium_crypto_sign($parameters->data, mmm_key($userID)))];
                }
            }
            return [false, "Unknown action"];
        }
        return [false, "Authentication error"];
    }, true);
}

function mmm_prepare($id)
{
    if (!file_exists(KEYPAIR_DIR . DIRECTORY_SEPARATOR . $id . ".key")) {
        $keypair = sodium_crypto_kx_keypair();
        $secretkey = sodium_crypto_kx_secretkey($keypair);
        file_put_contents(KEYPAIR_DIR . DIRECTORY_SEPARATOR . $id . ".key", $secretkey);
    }
}

function mmm_key($id)
{
    return file_get_contents(KEYPAIR_DIR . DIRECTORY_SEPARATOR . $id . ".key");
}