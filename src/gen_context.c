/**********************************************************************
 * Copyright (c) 2013, 2014, 2015 Pieter Wuille, Gregory Maxwell      *
 * Distributed under the MIT software license, see the accompanying   *
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.*
 **********************************************************************/

#if defined HAVE_CONFIG_H
#include "libsecp256k1-config.h"
#endif

#undef USE_ECMULT_STATIC_CONTEXT
#include "secp256k1.c"

int main(int argc, char **argv) {
    secp256k1_ecmult_gen_context_t ctx;
    uint8_t* ctx_bytes;
    int ctx_len;
    int i;
    FILE* fp;

    (void)argc;
    (void)argv;

    fp = fopen("src/ecmult_static_context.h","w");
    
    fprintf(fp, "#ifndef _SECP256K1_ECMULT_STATIC_CONTEXT_\n");
    fprintf(fp, "#define _SECP256K1_ECMULT_STATIC_CONTEXT_\n");
    fprintf(fp, "static const uint8_t secp256k1_ecmult_static_context[] = {\n");

    secp256k1_ecmult_gen_context_init(&ctx);
    secp256k1_ecmult_gen_context_build(&ctx);
    ctx_bytes = (uint8_t*)ctx.prec;
    ctx_len = sizeof(*ctx.prec);
    for (i = 0; i < ctx_len; i++) {
        fprintf(fp, "0x%02x", ctx_bytes[i]);
        if (i < (ctx_len - 1)) {
           fprintf(fp, ",");
        }
        if ((i % 20) == 19) {
            fprintf(fp, "\n");
        }
    }
    secp256k1_ecmult_gen_context_clear(&ctx);
    
    fprintf(fp, "};\n");
    fprintf(fp, "#endif\n");
    fclose(fp);
    
    return 0;
}
