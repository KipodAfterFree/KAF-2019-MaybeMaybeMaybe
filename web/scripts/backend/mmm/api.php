<?php
include_once __DIR__ . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . "base" . DIRECTORY_SEPARATOR . "api.php";
include_once __DIR__ . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . "authenticate" . DIRECTORY_SEPARATOR . "api.php";

const KEYPAIR_DIR = __DIR__ . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . ".." . DIRECTORY_SEPARATOR . "files" . DIRECTORY_SEPARATOR . "mmm" . DIRECTORY_SEPARATOR . "keys";

/**
 * HMAC Signing thing concept:
 * user sends in plaintext, we create a key - a 256byte random string, then send back the message in the following format:
 * ---START MMMHMACSIGN---
 * HMAC DIGEST
 * ---MESSAGE---
 * PLAINTEXT MESSAGE
 * ---END MMMHMACSIGN---
 * we verify in this way:
 * we take MESSAGE and compare its hmac digest to HMAC DIGEST
 */
function mmm()
{
    api("mmm", function ($action, $parameters) {
        $userID = authenticate();
        if ($userID !== null) {
            mmm_prepare($userID);
            if ($action === "sign") {
                if (isset($parameters->data)) {
                    return [true, mmm_hmac_sign($parameters->data, mmm_pkey($userID))];
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
        file_put_contents(KEYPAIR_DIR . DIRECTORY_SEPARATOR . $id . ".key", random(128));
    }
}

function mmm_pkey($id)
{
    return file_get_contents(KEYPAIR_DIR . DIRECTORY_SEPARATOR . $id . ".key");
}

function mmm_derive_key($key)
{
    $public = "";
    for ($i = 0; $i < strlen($key); $i += 2) {
        $public .= $key[$i];
    }
    return $public;
}

function mmm_hmac_verify($mmm, $key)
{
    $lines = explode("\n", $mmm);
    if (count($lines) >= 7) {
        if ($lines[0] === "---START MMM---" &&
            $lines[1] === "---PRIVATE DIGEST---" &&
            $lines[3] === "---PUBLIC DIGEST---" &&
            $lines[5] === "---MESSAGE---" &&
            $lines[count($lines) - 1] === "---END MMM---") {
            $private_hmac = $lines[2];
            $public_hmac = $lines[4];
            $data = "";
            for ($l = 6; $l < count($lines) - 1; $l++) {
                if (strlen($data) !== 0) {
                    $data .= "\n";
                }
                $data .= $lines[$l];
            }
            return mmm_hmac_calculate($data, $key) === $private_hmac && mmm_hmac_calculate($data, mmm_derive_key($key)) === $public_hmac;
        }
    }
    return false;
}

function mmm_hmac_sign($plaintext, $key)
{
    $string = "---BEGIN MMM---";
    $string .= "\n";
    $string .= "---PRIVATE DIGEST---";
    $string .= "\n";
    $string .= mmm_hmac_calculate($plaintext, $key);
    $string .= "\n";
    $string .= "---PUBLIC DIGEST---";
    $string .= "\n";
    $string .= mmm_hmac_calculate($plaintext, mmm_derive_key($key));
    $string .= "\n";
    $string .= "---MESSAGE---";
    $string .= "\n";
    $string .= $plaintext;
    $string .= "\n";
    $string .= "---END MMM---";
    return $string;
}

function mmm_hmac_calculate($plaintext, $key)
{
    return hash_hmac("sha256", $plaintext, $key);
}