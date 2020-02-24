#!/usr/bin/env bash
set -e

# Always run the tests on the same cpu.
CPU_AFFINITY="0"

GLOBAL_CONFIG="--enable-ecmult-static-precomputation --with-bignum=no --disable-openssl-tests"
COMPILERS=( "gcc-7" "gcc-8" "clang-6.0" "clang-7" "clang-8" "clang-9" )
OPT_LEVELS=( "-Os" "-O2" "-O3" "-Ofast" )
CONFIGS=( \
  "--with-asm=x86_64 --with-scalar=64bit --with-field=64bit" \
  "--with-asm=no --with-scalar=64bit --with-field=64bit" \
)

echo "Using global config: $GLOBAL_CONFIG"
echo
./autogen.sh >/dev/null
for i in "${COMPILERS[@]}"; do
  for j in "${CONFIGS[@]}"; do
    for k in "${OPT_LEVELS[@]}"; do
      echo "building and running bench with config: $i $j $k"
	    ./configure $GLOBAL_CONFIG $j CFLAGS="$k" CC="$i" >/dev/null && make clean >/dev/null && make bench_verify >/dev/null
			sha256sum bench_verify
      taskset -c $CPU_AFFINITY ./bench_verify
      echo
    done
  done
done
