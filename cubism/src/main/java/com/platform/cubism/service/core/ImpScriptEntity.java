package com.platform.cubism.service.core;

import static com.platform.cubism.util.StringUtils.hasText;

import com.platform.cubism.base.Json;
import com.platform.cubism.jdbc.SqlExecutor;
import com.platform.cubism.jdbc.SqlFactory;
import com.platform.cubism.jdbc.tran.TransactionStatus;
import com.platform.cubism.util.Assert;

public class ImpScriptEntity extends AbstractEntity {
	private TransactionStatus status;

	public ImpScriptEntity() {
		;
	}

	public ImpScriptEntity(ServiceEntity entity) {
		super(entity);
	}

	@Override
	public void setServiceEntity(ServiceEntity entity) {
		Assert.state(entity.getType() == EntityType.sql);
		setEntity(entity);
	}

	@Override
	public void setTransactionStatus(TransactionStatus status) {
		this.status = status;
	}

	public Json execute(Json in) throws Exception {
		String sql = getValue();
		if (!hasText(sql)) {
			return null;
		}

		SqlExecutor sqlExec = SqlFactory.getSqlExecutor(getServiceEntity(), status);
		if (sqlExec == null) {
			return null;
		}

		return sqlExec.execute(sql, in);
	}
}