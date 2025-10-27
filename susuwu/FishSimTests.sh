#!/bin/sh
#
# /* Attribution (henceforth "*this attribution*", whose syntax is *Markdown*): 2024 [Swudu Susuwu](https://swudususuwu.substack.com)
#  * <https://github.com/SwuduSusuwu/SusuLib/> has the newest version of `./susuwu/FishSimTests.sh` (henceforth "*this source code*").
#  * If *this attribution* is shown, *this source code* allows all uses. *This attribution* constitutes the most permissive which is compatible with [*GPLv2*](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) + [*Apache 2*](https://www.apache.org/licenses/LICENSE-2.0.html), which is suitable for personal use (also suitable for school use).
#  * If *this attribution* is not professional enough for business use: businesses can use *this source code* through included versions of [*GPLv2*](./LICENSE_GPLv2), [*Apache 2*](./LICENSE), or through both of those. */

GIT_ROOT="$(dirname "$(git rev-parse --git-dir)")/" #/* `git` does not set `${GIT_DIR}`, nor `${GIT_WORK_TREE}` */

test_class="${GIT_ROOT}susuwu/FishSim"
stock_log="${test_class}_stock.log"
log_to="${test_class}_local.log"
javac "${test_class}.java" 2> "${log_to}"
#java --source 22 "${test_class}.java" 2> "${log_to}"
success=$?

git diff --no-index "${stock_log}" "${log_to}"

exit ${success}

