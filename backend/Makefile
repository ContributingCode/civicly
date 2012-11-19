BOOTSTRAP_BASE_DIR = public/css
BOOTSTRAP_LESS = ${BOOTSTRAP_BASE_DIR}/less/bootstrap.less
BOOTSTRAP_RESPONSIVE_LESS = ${BOOTSTRAP_BASE_DIR}/less/responsive.less
LESS_COMPRESSOR ?= /usr/local/bin/lessc

bootstrap:
				${LESS_COMPRESSOR} ${BOOTSTRAP_LESS} > ${BOOTSTRAP_BASE_DIR}/style.css
				${LESS_COMPRESSOR} --compress ${BOOTSTRAP_LESS} > ${BOOTSTRAP_BASE_DIR}/style.min.css
				${LESS_COMPRESSOR} ${BOOTSTRAP_RESPONSIVE_LESS} > ${BOOTSTRAP_BASE_DIR}/style-responsive.css
				${LESS_COMPRESSOR} --compress ${BOOTSTRAP_RESPONSIVE_LESS} > ${BOOTSTRAP_BASE_DIR}/style-responsive.min.css
