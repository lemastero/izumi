package com.github.pshirshov.izumi.distage

import com.github.pshirshov.izumi.distage.model.Locator
import com.github.pshirshov.izumi.distage.model.plan.OrderedPlan
import com.github.pshirshov.izumi.distage.model.provisioning.PlanInterpreter
import com.github.pshirshov.izumi.distage.model.provisioning.Provision.ProvisionImmutable
import com.github.pshirshov.izumi.distage.model.references.IdentifiedRef
import com.github.pshirshov.izumi.distage.model.reflection.universe.RuntimeDIUniverse
import com.github.pshirshov.izumi.distage.model.reflection.universe.RuntimeDIUniverse.{SafeType, TagK}

final class LocatorDefaultImpl[F[_]]
(
  val plan: OrderedPlan,
  val parent: Option[Locator],
  val dependencyMap: ProvisionImmutable[F],
) extends AbstractLocator {
  override protected def unsafeLookup(key: RuntimeDIUniverse.DIKey): Option[Any] =
    dependencyMap.get(key)

  override protected[distage] def finalizers[F1[_]: TagK]: collection.Seq[PlanInterpreter.Finalizer[F1]] = {
    dependencyMap.finalizers
      .filter(_.fType == SafeType.getK[F1])
      .map(_.asInstanceOf[PlanInterpreter.Finalizer[F1]])
  }

  override def instances: collection.Seq[IdentifiedRef] =
    dependencyMap.enumerate
}