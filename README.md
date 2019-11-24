# MaybeMaybeMaybe

MaybeMaybeMaybe is an information security challenge in the Web/Crypto category, and was presented to participants of [KAF CTF 2019](https://ctf.kipodafterfree.com)

## Challenge story

No Story

## Challenge exploit

Path traversal in the upload frontend allows a user to overwrite the user key, then a user can upload an RCE and get the flag.

## Challenge solution

Not needed

## Building and installing

[Clone](https://github.com/NadavTasher/2019-MaybeMaybeMaybe/archive/master.zip) the repository, then type the following command to build the container:
```bash
docker build . -t mmm
```

To run the challenge, execute the following command:
```bash
docker run --rm -d -p 1100:80 -p 1101:8000 mmm
```

## Usage

You may now access the challenge interface through your browser: `http://localhost:1100`, and through `nc localhost 1101`

## Flag

Flag is:
```flagscript
KAF{m0mmy_a11ow3d_m3_t0_sign_it_my531f__}
```

## License
[MIT License](https://choosealicense.com/licenses/mit/)