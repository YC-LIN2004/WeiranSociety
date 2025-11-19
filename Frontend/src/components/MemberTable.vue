<template>
    <div class="table-wrapper">
        <div class="display-control">
            <label>顯示</label>
            <select v-model="localDisplayCount">
                <option>10</option>
                <option>20</option>
                <option>50</option>
            </select>
            筆結果
        </div>

        <table class="member-table">
            <thead>
                <tr>
                    <th>編號</th>
                    <th>註冊時間</th>
                    <th>姓名</th>
                    <th>顯示名稱</th>
                    <th>信箱</th>
                    <th>驗證</th>
                    <th>優惠通知</th>
                    <th>狀態</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(member, index) in members" :key="member.id">
                    <td>{{ index + 1 }}</td>
                    <td>{{ member.registeredAt }}</td>
                    <td>{{ member.name }}</td>
                    <td>{{ member.displayName }}</td>
                    <td>{{ member.email }}</td>
                    <td>{{ member.verified ? '已驗證' : '未驗證' }}</td>
                    <td>{{ member.promoNotice ? '是' : '否' }}</td>
                    <td :class="statusClass(member.status)">{{ statusLabel(member.status) }}</td>
                    <td>
                        <div class="action-buttons">
                            <button @click="$emit('change-status', member, 'active')" class="btn active">啟用</button>
                            <button @click="$emit('change-status', member, 'suspended')"
                                class="btn suspended">暫停</button>
                            <button @click="$emit('change-status', member, 'deleted')" class="btn deleted">註銷</button>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
export default {
    name: 'MemberTable',
    props: {
        members: Array,
        displayCount: Number,
    },
    data() {
        return {
            localDisplayCount: this.displayCount,
        };
    },
    watch: {
        localDisplayCount(val) {
            this.$emit('update:displayCount', Number(val));
        },
    },
    methods: {
        statusLabel(status) {
            switch (status) {
                case 'active':
                    return '正常';
                case 'suspended':
                    return '暫停';
                case 'deleted':
                    return '已刪除';
                default:
                    return '未知';
            }
        },
        statusClass(status) {
            return {
                active: status === 'active',
                suspended: status === 'suspended',
                deleted: status === 'deleted',
            };
        },
    },
};
</script>

<style scoped>
.table-wrapper {
    background-color: #fff;
    padding: 15px 20px;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
    margin-top: 20px;

    .display-control {
        margin-bottom: 10px;
        display: flex;
        align-items: center;
        gap: 8px;

        select {
            padding: 4px 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
    }

    .member-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 14px;

        thead {
            background-color: #f0f0f0;

            th {
                text-align: left;
                padding: 10px;
                border-bottom: 2px solid #ccc;
            }
        }

        tbody {
            tr {
                &:nth-child(even) {
                    background-color: #fafafa;
                }

                td {
                    padding: 10px;
                    border-bottom: 1px solid #eee;

                    &.active {
                        color: green;
                        font-weight: bold;
                    }

                    &.suspended {
                        color: orange;
                        font-weight: bold;
                    }

                    &.deleted {
                        color: red;
                        font-weight: bold;
                    }
                }
            }
        }

        .action-buttons {
            display: flex;
            gap: 5px;

            .btn {
                padding: 5px 10px;
                border: none;
                border-radius: 4px;
                font-size: 13px;
                cursor: pointer;
                transition: background-color 0.2s ease;

                &.active {
                    background-color: #28a745;
                    color: white;

                    &:hover {
                        background-color: #218838;
                    }
                }

                &.suspended {
                    background-color: #ffc107;
                    color: white;

                    &:hover {
                        background-color: #e0a800;
                    }
                }

                &.deleted {
                    background-color: #dc3545;
                    color: white;

                    &:hover {
                        background-color: #c82333;
                    }
                }
            }
        }
    }
}
</style>
